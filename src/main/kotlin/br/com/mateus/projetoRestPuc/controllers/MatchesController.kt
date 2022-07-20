package br.com.mateus.projetoRestPuc.controllers

import br.com.mateus.projetoRestPuc.dtos.MatchUpdDto
import br.com.mateus.projetoRestPuc.dtos.PlayerDto
import br.com.mateus.projetoRestPuc.dtos.PlayerUpdDto
import br.com.mateus.projetoRestPuc.entities.EventEntity
import br.com.mateus.projetoRestPuc.entities.MatchEntity
import br.com.mateus.projetoRestPuc.entities.PlayerEntity
import br.com.mateus.projetoRestPuc.entities.TypeEventEnum
import br.com.mateus.projetoRestPuc.rabbit.QueueSender
import br.com.mateus.projetoRestPuc.response.Response
import br.com.mateus.projetoRestPuc.services.EventService
import br.com.mateus.projetoRestPuc.services.MatchService
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/matches")
class MatchesController(val matchService: MatchService, val eventService: EventService, val queueSender: QueueSender?) {

    @ApiOperation("Return Matches")
    @ApiResponses(value = [ApiResponse(code = 200, message = "Successful request"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(method = [RequestMethod.GET])
    fun findMatches(): ResponseEntity<List<MatchEntity>> {
        val matches: List<MatchEntity> = matchService.findMatches()
        return ResponseEntity.ok(matches)
    }

    @ApiOperation("Return Match by id")
    @ApiResponses(value = [ApiResponse(code = 200, message = "Successful request"),
        ApiResponse(code = 404, message = "Not found"),
        ApiResponse(code = 400, message = "Parameter not informed"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}"], method = [RequestMethod.GET])
    fun findMatchId(@PathVariable id: Int?): ResponseEntity<MatchEntity> {

        if (id == null) {
            return ResponseEntity.badRequest().build()
        }

        val match: Optional<MatchEntity> = matchService.findMatchById(id)
        if(match.isEmpty) {
            return ResponseEntity.notFound().build()
        }
        return ResponseEntity.ok(match.get())
    }

    @ApiOperation("Update Result a Match")
    @ApiResponses(value = [ApiResponse(code = 204, message = "Updated Match"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
        ApiResponse(code = 404, message = "Match not exists"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}"], method = [RequestMethod.PATCH])
    fun updateMatch(@Valid @RequestBody matchDto: MatchUpdDto, @PathVariable id: Int?): ResponseEntity<String> {

        if (id == null) {
            return ResponseEntity.badRequest().build()
        }

        val matchExists = matchService.findMatchById(id)
        if(matchExists.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        matchExists.get().result = matchDto.result

        matchService.persist(matchExists.get())
        return ResponseEntity.noContent().build()
    }

    @ApiOperation("Delete a Match")
    @ApiResponses(value = [ApiResponse(code = 200, message = "Deleted Match"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
        ApiResponse(code = 404, message = "Match not exists"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}"], method = [RequestMethod.DELETE])
    fun delMatch(@PathVariable id: Int?): ResponseEntity<Response<Void>> {

        if (id == null) {
            return ResponseEntity.badRequest().build()
        }

        val matchExists = matchService.findMatchById(id)
        if(matchExists.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        matchService.delete(id)
        return ResponseEntity.ok().build()
    }

    @ApiOperation("Notification start of a match")
    @ApiResponses(value = [ApiResponse(code = 201, message = "Notification Created - New resource URI in header"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value=["/{id}/events/start"], method = [RequestMethod.POST])
    fun startMatch(@PathVariable id: Int?): ResponseEntity<Response<EventEntity>> {
        val response: Response<EventEntity> = Response()

        if (id == null) {
            return ResponseEntity.badRequest().build()
        }

        val match: Optional<MatchEntity> = matchService.findMatchById(id)
        if(match.isEmpty) {
            response.erros.add("Match not exists!")
        }

        if(response.erros.size>0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }

        val message = "A partida entre ${match.get().matchHomeTeam?.name} e ${match.get().matchAwayTeam?.name} do torneio ${match.get().matchTournament?.name}"
        var event: EventEntity = EventEntity(null, "$message começou!", match.get(), TypeEventEnum.INICIO);
        event = eventService.persist(event)

        queueSender?.send(event.message)

        val uri: URI = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(event.id).toUri()

        response.data = event
        return ResponseEntity.created(uri).body(response)
    }

    @ApiOperation("Notification end of a match")
    @ApiResponses(value = [ApiResponse(code = 201, message = "Notification Created - New resource URI in header"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value=["/{id}/events/end"], method = [RequestMethod.POST])
    fun endMatch(@PathVariable id: Int?): ResponseEntity<Response<EventEntity>> {
        val response: Response<EventEntity> = Response()

        if (id == null) {
            return ResponseEntity.badRequest().build()
        }

        val match: Optional<MatchEntity> = matchService.findMatchById(id)
        if(match.isEmpty) {
            response.erros.add("Match not exists!")
        }

        if(response.erros.size>0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }

        val message = "A partida entre ${match.get().matchHomeTeam?.name} e ${match.get().matchAwayTeam?.name} do torneio ${match.get().matchTournament?.name}"
        var event: EventEntity = EventEntity(null, "$message acabou!", match.get(), TypeEventEnum.FIM);
        event = eventService.persist(event)

        queueSender?.send(event.message)

        val uri: URI = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(event.id).toUri()

        response.data = event
        return ResponseEntity.created(uri).body(response)
    }

    @ApiOperation("Notification halftime of a match")
    @ApiResponses(value = [ApiResponse(code = 201, message = "Notification Created - New resource URI in header"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value=["/{id}/events/halftime"], method = [RequestMethod.POST])
    fun halftimeMatch(@PathVariable id: Int?): ResponseEntity<Response<EventEntity>> {
        val response: Response<EventEntity> = Response()

        if (id == null) {
            return ResponseEntity.badRequest().build()
        }

        val match: Optional<MatchEntity> = matchService.findMatchById(id)
        if(match.isEmpty) {
            response.erros.add("Match not exists!")
        }

        if(response.erros.size>0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }

        val message = "A partida entre ${match.get().matchHomeTeam?.name} e ${match.get().matchAwayTeam?.name} do torneio ${match.get().matchTournament?.name}"
        var event: EventEntity = EventEntity(null, "$message acabou o primeiro tempo!", match.get(), TypeEventEnum.INTERVALO);

        event = eventService.persist(event)

        queueSender?.send(event.message)

        val uri: URI = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(event.id).toUri()

        response.data = event
        return ResponseEntity.created(uri).body(response)
    }

}