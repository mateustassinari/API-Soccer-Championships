package br.com.mateus.projetoRestPuc.controllers

import br.com.mateus.projetoRestPuc.dtos.*
import br.com.mateus.projetoRestPuc.entities.MatchEntity
import br.com.mateus.projetoRestPuc.entities.PlayerEntity
import br.com.mateus.projetoRestPuc.entities.TeamEntity
import br.com.mateus.projetoRestPuc.entities.TournamentEntity
import br.com.mateus.projetoRestPuc.response.Response
import br.com.mateus.projetoRestPuc.services.MatchService
import br.com.mateus.projetoRestPuc.services.TeamService
import br.com.mateus.projetoRestPuc.services.TournamentService
import br.com.mateus.projetoRestPuc.utils.Utils
import com.fasterxml.jackson.databind.deser.DataFormatReaders
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/tournaments")
class TournamentController(val tournamentService: TournamentService, val teamService: TeamService, val matchService: MatchService, val utils: Utils) {

    @ApiOperation("Return Tournaments")
    @ApiResponses(
        value = [ApiResponse(code = 200, message = "Successful request"),
            ApiResponse(code = 500, message = "Unexpected error")]
    )
    @RequestMapping(method = [RequestMethod.GET])
    fun findTournaments(): ResponseEntity<List<TournamentEntity>> {
        val tournaments: List<TournamentEntity> = tournamentService.findTournaments()
        return ResponseEntity.ok(tournaments)
    }

    @ApiOperation("Return Tournament by id")
    @ApiResponses(
        value = [ApiResponse(code = 200, message = "Successful request"),
            ApiResponse(code = 404, message = "Not found"),
            ApiResponse(code = 400, message = "Parameter not informed"),
            ApiResponse(code = 500, message = "Unexpected error")]
    )
    @RequestMapping(value = ["/{id}"], method = [RequestMethod.GET])
    fun findTournamentId(@PathVariable id: Int?): ResponseEntity<TournamentEntity> {

        if (id == null) {
            return ResponseEntity.badRequest().build()
        }

        val tournament: Optional<TournamentEntity> = tournamentService.findTournamentById(id)
        if (tournament.isEmpty) {
            return ResponseEntity.notFound().build()
        }
        return ResponseEntity.ok(tournament.get())
    }

    @ApiOperation("Register a new Tournament")
    @ApiResponses(value = [ApiResponse(code = 201, message = "Tournament Created - New resource URI in header"),
        ApiResponse(code = 409, message = "Tournament already exists"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(method = [RequestMethod.POST])
    fun addTournament(@Valid @RequestBody tournamentDto: TournamentDto): ResponseEntity<Response<TournamentEntity>> {
        val response: Response<TournamentEntity> = Response()

        val tournamentExists: Optional<TournamentEntity> = tournamentService.findTournamentByName(tournamentDto.name!!)
        if(!tournamentExists.isEmpty) {
            response.erros.add("Tournament already exists!")
        }

        val startDate: Date? = utils.parseDate(tournamentDto.startDate!!)
        val endDate: Date? = utils.parseDate(tournamentDto.endDate!!)

        if(startDate == null)
            response.erros.add("startDate must be in format dd/MM/yyyy and be valid!")

        if(endDate == null)
            response.erros.add("endDate must be in format dd/MM/yyyy and be valid!")

        if(tournamentDto.qtdTeams!! < 2) {
            response.erros.add("qtdTeams it has to be greater than 2")
        }

        if(tournamentDto.teamsTournament!!.size > tournamentDto.qtdTeams!!) {
            response.erros.add("teamsTournament must have the same or lesser amount of qtdTeams")
        }

        if(startDate != null && endDate != null) {

            if(endDate.before(startDate)) {
                response.erros.add("endDate must be after that startDate!")
            }

            var tournament: TournamentEntity = tournamentService.convertDtoToNewTournament(tournamentDto, startDate, endDate, response)

            if(response.erros.size>0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
            }

            tournament = tournamentService.persist(tournament)

            val uri: URI = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(tournament.id).toUri()

            response.data = tournament
            return ResponseEntity.created(uri).body(response)

        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ApiOperation("Update name, startDate, endDate of a Tournament")
    @ApiResponses(value = [ApiResponse(code = 204, message = "Updated Tournament"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
        ApiResponse(code = 404, message = "Tournament not exists"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}"], method = [RequestMethod.PATCH])
    fun updateTournament(@Valid @RequestBody tournamentDto: TournamentUpdDto, @PathVariable id: Int?): ResponseEntity<Response<TournamentEntity>> {

        if (id == null) {
            return ResponseEntity.badRequest().build()
        }

        val response: Response<TournamentEntity> = Response()

        val tournamentExists = tournamentService.findTournamentById(id)
        if(tournamentExists.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        val startDate: Date? = utils.parseDate(tournamentDto.startDate!!)
        val endDate: Date? = utils.parseDate(tournamentDto.endDate!!)

        if(startDate == null)
            response.erros.add("startDate must be in format dd/MM/yyyy and be valid!")

        if(endDate == null)
            response.erros.add("endDate must be in format dd/MM/yyyy and be valid!")


        if(startDate != null && endDate != null) {

            if(endDate.before(startDate)) {
                response.erros.add("endDate must be after that startDate!")
            }

            if(response.erros.size>0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
            }

            tournamentExists.get().name = tournamentDto.name
            tournamentExists.get().startDate = java.sql.Date(startDate.time)
            tournamentExists.get().endDate = java.sql.Date(endDate.time)

            tournamentService.persist(tournamentExists.get())
            return ResponseEntity.noContent().build()

        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ApiOperation("Delete a Tournament")
    @ApiResponses(value = [ApiResponse(code = 200, message = "Deleted Tournament"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
        ApiResponse(code = 404, message = "Tournament not exists"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}"], method = [RequestMethod.DELETE])
    fun delTournament(@PathVariable id: Int?): ResponseEntity<Response<Void>> {

        if (id == null) {
            return ResponseEntity.badRequest().build()
        }

        val tournamentExists = tournamentService.findTournamentById(id)
        if(tournamentExists.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        tournamentService.delete(id)
        return ResponseEntity.ok().build()
    }

    @ApiOperation("Add a Team in a Tournament")
    @ApiResponses(value = [ApiResponse(code = 200, message = "Added Team in the Tournament"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
    ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}/team/{teamId}"], method = [RequestMethod.POST])
    fun addTeamTournament(@PathVariable id: Int?,@PathVariable teamId: Int?): ResponseEntity<Response<TournamentEntity>> {

        if (id == null || teamId == null) {
            return ResponseEntity.badRequest().build()
        }

        val response: Response<TournamentEntity> = Response()

        val tournamentExists = tournamentService.findTournamentById(id)
        if(tournamentExists.isEmpty) {
            response.erros.add("Tournament not exists!")
        }

        val teamExists = teamService.findTeamById(teamId)
        if(teamExists.isEmpty) {
            response.erros.add("Team not exists!")
        }

        if(tournamentExists.isPresent && tournamentExists.get().teamsTournament?.size == tournamentExists.get().qtdTeams) {
            response.erros.add("Tournament already has the maximum number of teams!")
        }

        if(response.erros.size>0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }

        val tournamentWithNewTeam = tournamentService.addTeamTournament(teamExists.get(),tournamentExists.get())
        response.data = tournamentWithNewTeam

        return ResponseEntity.ok().body(response)
    }

    @ApiOperation("Delete a Team in a Tournament")
    @ApiResponses(value = [ApiResponse(code = 200, message = "Deleted Team in the Tournament"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
        ApiResponse(code = 404, message = "Tournament or Team not exists"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}/team/{teamId}"], method = [RequestMethod.DELETE])
    fun delTeamTournament(@PathVariable id: Int?,@PathVariable teamId: Int?): ResponseEntity<Void> {

        if (id == null || teamId == null) {
            return ResponseEntity.badRequest().build()
        }

        val tournamentExists = tournamentService.findTournamentById(id)
        if(tournamentExists.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        val teamExists = teamService.findTeamById(teamId)
        if(teamExists.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        tournamentService.deleteTeamTournament(teamExists.get(),tournamentExists.get())
        return ResponseEntity.ok().build()
    }

    @ApiOperation("Return Matches of a Tournament by id")
    @ApiResponses(value = [ApiResponse(code = 200, message = "Successful request"),
        ApiResponse(code = 400, message = "Parameter not informed"),
        ApiResponse(code = 404, message = "Tournament not exists"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}/matches"],method = [RequestMethod.GET])
    fun findTournamentsMatches(@PathVariable id: Int?): ResponseEntity<List<MatchEntity>> {

        if (id == null) {
            return ResponseEntity.badRequest().build()
        }

        val tournamentExists = tournamentService.findTournamentById(id)
        if(tournamentExists.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        val tournaments: List<MatchEntity> = tournamentService.findTournamentMatches(id)

        return ResponseEntity.ok(tournaments)
    }

    @ApiOperation("Register a new Match of a Tournament by id")
    @ApiResponses(value = [ApiResponse(code = 201, message = "Match Created - New resource URI in header"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}/matches"], method = [RequestMethod.POST])
    fun addMatchTournament(@PathVariable id: Int?, @Valid @RequestBody matchDto: MatchDto): ResponseEntity<Response<MatchEntity>> {
        val response: Response<MatchEntity> = Response()

        if (id == null) {
            return ResponseEntity.badRequest().build()
        }

        val tournament: Optional<TournamentEntity> = tournamentService.findTournamentById(id)
        if(tournament.isEmpty) {
            response.erros.add("Tournament not exists!")
        }

        val matchAwayTeam: Optional<TeamEntity> = teamService.findTeamById(matchDto.matchAwayTeam!!)
        if(matchAwayTeam.isEmpty) {
            response.erros.add("Away Team not exists!")
        }

        val matchHomeTeam: Optional<TeamEntity> = teamService.findTeamById(matchDto.matchHomeTeam!!)
        if(matchHomeTeam.isEmpty) {
            response.erros.add("Home Team not exists!")
        }

        if(response.erros.size>0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }

        var match: MatchEntity = matchService.convertDtoToNewMatch(matchDto, tournament.get(), matchAwayTeam.get(), matchHomeTeam.get())
        match = matchService.persist(match)

        val uri: URI = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(match.id).toUri()

        response.data = match
        return ResponseEntity.created(uri).body(response)
    }


}
