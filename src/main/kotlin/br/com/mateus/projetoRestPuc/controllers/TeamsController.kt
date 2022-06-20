package br.com.mateus.projetoRestPuc.controllers

import br.com.mateus.projetoRestPuc.dtos.*
import br.com.mateus.projetoRestPuc.entities.PlayerEntity
import br.com.mateus.projetoRestPuc.entities.TeamEntity
import br.com.mateus.projetoRestPuc.response.Response
import br.com.mateus.projetoRestPuc.services.PlayerService
import br.com.mateus.projetoRestPuc.services.TeamService
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*
import javax.validation.Valid


@RestController
@RequestMapping("/teams")
class TeamsController(val teamService: TeamService, val playerService: PlayerService) {

    @ApiOperation("Return Teams")
    @ApiResponses(value = [ApiResponse(code = 200, message = "Successful request"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(method = [RequestMethod.GET])
    fun findTeams(): ResponseEntity<List<TeamEntity>> {
        val teams: List<TeamEntity> = teamService.findTeams()
        return ResponseEntity.ok(teams)
    }

    @ApiOperation("Return Team by id")
    @ApiResponses(value = [ApiResponse(code = 200, message = "Successful request"),
        ApiResponse(code = 404, message = "Not found"),
        ApiResponse(code = 400, message = "Parameter not informed"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}"], method = [RequestMethod.GET])
    fun findTeamId(@PathVariable id: Int?): ResponseEntity<TeamEntity> {

        if (id == null) {
            return ResponseEntity.badRequest().build()
        }

        val team: Optional<TeamEntity> = teamService.findTeamById(id)
        if(team.isEmpty) {
            return ResponseEntity.notFound().build()
        }
        return ResponseEntity.ok(team.get())
    }

    @ApiOperation("Register a new Team")
    @ApiResponses(value = [ApiResponse(code = 201, message = "Team Created - New resource URI in header"),
        ApiResponse(code = 409, message = "Team already exists"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(method = [RequestMethod.POST])
    fun addTeam(@Valid @RequestBody teamDto: TeamDto): ResponseEntity<Response<TeamEntity>> {
        val response: Response<TeamEntity> = Response()
        var date: Date? = null

        val teamExists: Optional<TeamEntity> = teamService.findTeamByNameAndUfAndCity(teamDto.name!!,teamDto.uf!!,teamDto.city!!)
        if(!teamExists.isEmpty) {
            response.erros.add("Team already exists!")
        }

        try {
            val format = SimpleDateFormat("dd/MM/yyyy")
            format.isLenient = false
            date = format.parse(teamDto.foundingDate)
        } catch (e: Exception) {
            response.erros.add("foundingDate is must be in format dd/MM/yyyy and be valid!")
        }

        if(response.erros.size>0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }

        var team: TeamEntity = teamService.convertDtoToNewTeam(teamDto, date!!)
        team = teamService.persist(team)

        val uri: URI = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(team.id).toUri()

        response.data = team
        return ResponseEntity.created(uri).body(response)
    }

    @ApiOperation("Update a Team")
    @ApiResponses(value = [ApiResponse(code = 204, message = "Updated Team"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}"], method = [RequestMethod.PATCH])
    fun updateTeam(@Valid @RequestBody teamDto: TeamUpdDto, @PathVariable id: Int?): ResponseEntity<String> {

        if (id == null) {
            return ResponseEntity.badRequest().build()
        }

        val teamExists = teamService.findTeamById(id)
        if(teamExists.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        teamExists.get().name = teamDto.name

        teamService.persist(teamExists.get())
        return ResponseEntity.noContent().build()
    }

    @ApiOperation("Delete a Team")
    @ApiResponses(value = [ApiResponse(code = 200, message = "Deleted Team"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}"], method = [RequestMethod.DELETE])
    fun delTeam(@PathVariable id: Int?): ResponseEntity<Response<Void>> {

        if (id == null) {
            return ResponseEntity.badRequest().build()
        }

        val teamExists = teamService.findTeamById(id)
        if(teamExists.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        teamService.delete(id)
        return ResponseEntity.ok().build()
    }

    @ApiOperation("Register a new Player of a Team by id")
    @ApiResponses(value = [ApiResponse(code = 201, message = "Player Created - New resource URI in header"),
        ApiResponse(code = 409, message = "Player already exists"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}/players"], method = [RequestMethod.POST])
    fun addTeamPlayer(@PathVariable id: Int?, @Valid @RequestBody playerDto: PlayerDto): ResponseEntity<Response<PlayerEntity>> {
        val response: Response<PlayerEntity> = Response()
        var date: Date? = null

        if (id == null) {
            return ResponseEntity.badRequest().build()
        }

        val playerExists: Optional<PlayerEntity> = playerService.findPlayerByCode(playerDto.codePlayer!!)
        if(!playerExists.isEmpty) {
            response.erros.add("Player already exists!")
        }

        try {
            val format = SimpleDateFormat("dd/MM/yyyy")
            format.isLenient = false
            date = format.parse(playerDto.birthDate)
        } catch (e: Exception) {
            response.erros.add("birthDate is must be in format dd/MM/yyyy and be valid!")
        }

        val team: Optional<TeamEntity> = teamService.findTeamById(id)
        if(team.isEmpty) {
            response.erros.add("Team not exists!")
        }

        if(response.erros.size>0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }

        var player: PlayerEntity = playerService.convertDtoToNewPlayer(playerDto, date!!, team.get())
        player = playerService.persist(player)

        val uri: URI = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(player.id).toUri()

        response.data = player
        return ResponseEntity.created(uri).body(response)
    }

    @ApiOperation("Return Players of a Team by id")
    @ApiResponses(value = [ApiResponse(code = 200, message = "Successful request"),
        ApiResponse(code = 400, message = "Parameter not informed"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}/players"],method = [RequestMethod.GET])
    fun findPlayersTeam(@PathVariable id: Int?): ResponseEntity<List<PlayerEntity>> {

        if (id == null) {
            return ResponseEntity.badRequest().build()
        }

        val players: List<PlayerEntity> = teamService.findTeamPlayers(id)

        return ResponseEntity.ok(players)
    }

    @ApiOperation("Return Transfers of a Team by id")
    @ApiResponses(value = [ApiResponse(code = 200, message = "Successful request"),
        ApiResponse(code = 400, message = "Parameter not informed"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}/transfers"],method = [RequestMethod.GET])
    fun findTransfersTeam(@PathVariable id: Int?): ResponseEntity<TeamTransfersDto> {

        if (id == null) {
            return ResponseEntity.badRequest().build()
        }

        val transfers: TeamTransfersDto = teamService.findTeamTransfers(id)

        return ResponseEntity.ok(transfers)
    }

}