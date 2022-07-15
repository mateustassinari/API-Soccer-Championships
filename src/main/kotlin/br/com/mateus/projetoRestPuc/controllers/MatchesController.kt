package br.com.mateus.projetoRestPuc.controllers

import br.com.mateus.projetoRestPuc.dtos.MatchUpdDto
import br.com.mateus.projetoRestPuc.dtos.PlayerUpdDto
import br.com.mateus.projetoRestPuc.entities.MatchEntity
import br.com.mateus.projetoRestPuc.entities.PlayerEntity
import br.com.mateus.projetoRestPuc.response.Response
import br.com.mateus.projetoRestPuc.services.MatchService
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/matches")
class MatchesController(val matchService: MatchService) {

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

}