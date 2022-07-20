package br.com.mateus.projetoRestPuc.controllers

import br.com.mateus.projetoRestPuc.dtos.*
import br.com.mateus.projetoRestPuc.entities.*
import br.com.mateus.projetoRestPuc.rabbit.QueueSender
import br.com.mateus.projetoRestPuc.response.Response
import br.com.mateus.projetoRestPuc.services.EventService
import br.com.mateus.projetoRestPuc.services.MatchService
import br.com.mateus.projetoRestPuc.services.TeamService
import br.com.mateus.projetoRestPuc.services.TournamentService
import br.com.mateus.projetoRestPuc.utils.Utils
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
class TournamentController(val tournamentService: TournamentService, val teamService: TeamService, val matchService: MatchService,
                           val eventService: EventService, val queueSender: QueueSender, val utils: Utils) {

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
    @ApiResponses(value = [ApiResponse(code = 200, message = "Updated Tournament"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
        ApiResponse(code = 404, message = "Tournament not exists"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}"], method = [RequestMethod.PATCH])
    fun updateTournament(@Valid @RequestBody tournamentDto: TournamentUpdDto, @PathVariable id: Int?): ResponseEntity<Response<TournamentEntity>> {
        val response: Response<TournamentEntity> = Response()

        if (id == null) {
            return ResponseEntity.badRequest().build()
        }

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
            response.data=tournamentExists.get()
            return ResponseEntity.ok().body(response)

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

    @ApiOperation("Return Match by id")
    @ApiResponses(value = [ApiResponse(code = 200, message = "Successful request"),
        ApiResponse(code = 404, message = "Match or Tournament not exists"),
        ApiResponse(code = 400, message = "Parameter not informed"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}/matches/{matchId}"], method = [RequestMethod.GET])
    fun findMatchId(@PathVariable id: Int?,@PathVariable matchId: Int?): ResponseEntity<Response<MatchEntity>> {
        val response: Response<MatchEntity> = Response()

        if (id == null || matchId == null) {
            return ResponseEntity.badRequest().build()
        }

        val tournamentExists = tournamentService.findTournamentById(id)
        if(tournamentExists.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        val matchExists = matchService.findMatchById(matchId)
        if(matchExists.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        if(matchExists.get().matchTournament?.id != id) {
            response.erros.add("This match does not belong to this tournament")
            return ResponseEntity.badRequest().body(response)
        }

        response.data = matchExists.get()
        return ResponseEntity.ok(response)
    }

    @ApiOperation("Update Result a Match")
    @ApiResponses(value = [ApiResponse(code = 200, message = "Updated Match"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
        ApiResponse(code = 404, message = "Match or Tournament not exists"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}/matches/{matchId}"], method = [RequestMethod.PATCH])
    fun updateMatch(@Valid @RequestBody matchDto: MatchUpdDto, @PathVariable id: Int?, @PathVariable matchId: Int?): ResponseEntity<Response<MatchEntity>> {
        val response: Response<MatchEntity> = Response()

        if (id == null || matchId == null) {
            return ResponseEntity.badRequest().build()
        }

        val tournamentExists = tournamentService.findTournamentById(id)
        if(tournamentExists.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        val matchExists = matchService.findMatchById(matchId)
        if(matchExists.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        if(matchExists.get().matchTournament?.id != id) {
            response.erros.add("This match does not belong to this tournament")
            return ResponseEntity.badRequest().body(response)
        }

        matchExists.get().result = matchDto.result
        matchService.persist(matchExists.get())
        response.data = matchExists.get()
        return ResponseEntity.ok().body(response)
    }

    @ApiOperation("Delete a Match")
    @ApiResponses(value = [ApiResponse(code = 200, message = "Deleted Match"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
        ApiResponse(code = 404, message = "Match or Tournament not exists"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}/matches/{matchId}"], method = [RequestMethod.DELETE])
    fun delMatch(@PathVariable id: Int?, @PathVariable matchId: Int?): ResponseEntity<String> {

        if (id == null || matchId == null) {
            return ResponseEntity.badRequest().build()
        }

        val tournamentExists = tournamentService.findTournamentById(id)
        if(tournamentExists.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        val matchExists = matchService.findMatchById(matchId)
        if(matchExists.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        if(matchExists.get().matchTournament?.id != id) {
            return ResponseEntity.badRequest().body("This match does not belong to this tournament")
        }


        matchService.delete(id)
        return ResponseEntity.ok().build()
    }

    @ApiOperation("Notification start of a match")
    @ApiResponses(value = [ApiResponse(code = 201, message = "Notification Created - New resource URI in header"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
        ApiResponse(code = 404, message = "Match or Tournament not exists"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value=["/{id}/matches/{matchId}/events/start"], method = [RequestMethod.POST])
    fun startMatch(@PathVariable id: Int?, @PathVariable matchId: Int?): ResponseEntity<Response<EventEntity>> {
        val response: Response<EventEntity> = Response()

        if (id == null || matchId == null) {
            return ResponseEntity.badRequest().build()
        }

        val tournamentExists = tournamentService.findTournamentById(id)
        if(tournamentExists.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        val matchExists: Optional<MatchEntity> = matchService.findMatchById(matchId)
        if(matchExists.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        if(matchExists.get().matchTournament?.id != id) {
            response.erros.add("This match does not belong to this tournament")
            return ResponseEntity.badRequest().body(response)
        }

        val message = "A partida entre ${matchExists.get().matchHomeTeam?.name} e ${matchExists.get().matchAwayTeam?.name} do torneio ${matchExists.get().matchTournament?.name}"
        var event = EventEntity(null, "$message começou!", matchExists.get(), TypeEventEnum.INICIO);
        event = eventService.persist(event)

        queueSender.send(event.message)

        val uri: URI = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(event.id).toUri()

        response.data = event
        return ResponseEntity.created(uri).body(response)
    }

    @ApiOperation("Notification end of a match")
    @ApiResponses(value = [ApiResponse(code = 201, message = "Notification Created - New resource URI in header"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
        ApiResponse(code = 404, message = "Match or Tournament not exists"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value=["/{id}/matches/{matchId}/events/end"], method = [RequestMethod.POST])
    fun endMatch(@PathVariable id: Int?, @PathVariable matchId: Int?): ResponseEntity<Response<EventEntity>> {
        val response: Response<EventEntity> = Response()

        if (id == null || matchId == null) {
            return ResponseEntity.badRequest().build()
        }

        val tournamentExists = tournamentService.findTournamentById(id)
        if(tournamentExists.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        val matchExists: Optional<MatchEntity> = matchService.findMatchById(matchId)
        if(matchExists.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        if(matchExists.get().matchTournament?.id != id) {
            response.erros.add("This match does not belong to this tournament")
            return ResponseEntity.badRequest().body(response)
        }

        val message = "A partida entre ${matchExists.get().matchHomeTeam?.name} e ${matchExists.get().matchAwayTeam?.name} do torneio ${matchExists.get().matchTournament?.name}"
        var event = EventEntity(null, "$message acabou!", matchExists.get(), TypeEventEnum.FIM);
        event = eventService.persist(event)

        queueSender.send(event.message)

        val uri: URI = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(event.id).toUri()

        response.data = event
        return ResponseEntity.created(uri).body(response)
    }

    @ApiOperation("Notification halftime of a match")
    @ApiResponses(value = [ApiResponse(code = 201, message = "Notification Created - New resource URI in header"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
        ApiResponse(code = 404, message = "Match or Tournament not exists"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value=["/{id}/matches/{matchId}/events/halftime"], method = [RequestMethod.POST])
    fun halftimeMatch(@PathVariable id: Int?, @PathVariable matchId: Int?): ResponseEntity<Response<EventEntity>> {
        val response: Response<EventEntity> = Response()

        if (id == null || matchId == null) {
            return ResponseEntity.badRequest().build()
        }

        val tournamentExists = tournamentService.findTournamentById(id)
        if(tournamentExists.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        val matchExists: Optional<MatchEntity> = matchService.findMatchById(matchId)
        if(matchExists.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        if(matchExists.get().matchTournament?.id != id) {
            response.erros.add("This match does not belong to this tournament")
            return ResponseEntity.badRequest().body(response)
        }

        val message = "A partida entre ${matchExists.get().matchHomeTeam?.name} e ${matchExists.get().matchAwayTeam?.name} do torneio ${matchExists.get().matchTournament?.name}"
        var event = EventEntity(null, "$message acabou o primeiro tempo!", matchExists.get(), TypeEventEnum.INTERVALO);

        event = eventService.persist(event)

        queueSender.send(event.message)

        val uri: URI = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(event.id).toUri()

        response.data = event
        return ResponseEntity.created(uri).body(response)
    }

    @ApiOperation("Return Event by id")
    @ApiResponses(value = [ApiResponse(code = 200, message = "Successful request"),
        ApiResponse(code = 404, message = "Match, Tournament or Event not exists"),
        ApiResponse(code = 400, message = "Parameter not informed"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}/matches/{matchId}/events/{eventId}"], method = [RequestMethod.GET])
    fun findEventId(@PathVariable id: Int?,@PathVariable matchId: Int?,@PathVariable eventId: Int?): ResponseEntity<Response<EventEntity>> {
        val response: Response<EventEntity> = Response()

        if (id == null || matchId == null || eventId == null) {
            return ResponseEntity.badRequest().build()
        }

        val tournamentExists = tournamentService.findTournamentById(id)
        if(tournamentExists.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        val matchExists = matchService.findMatchById(matchId)
        if(matchExists.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        val eventExists = eventService.findEventById(eventId)
        if(eventExists.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        if(matchExists.get().matchTournament?.id != id) {
            response.erros.add("This match does not belong to this tournament")
            return ResponseEntity.badRequest().body(response)
        }

        if(eventExists.get().matchEvent?.id != matchId) {
            response.erros.add("This event does not belong to this match")
            return ResponseEntity.badRequest().body(response)
        }

        response.data = eventExists.get()
        return ResponseEntity.ok(response)
    }


}
