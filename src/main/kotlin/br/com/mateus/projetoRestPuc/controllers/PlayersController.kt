package br.com.mateus.projetoRestPuc.controllers

import br.com.mateus.projetoRestPuc.dtos.*
import br.com.mateus.projetoRestPuc.entities.PlayerEntity
import br.com.mateus.projetoRestPuc.entities.TeamEntity
import br.com.mateus.projetoRestPuc.entities.TransferEntity
import br.com.mateus.projetoRestPuc.response.Response
import br.com.mateus.projetoRestPuc.services.PlayerService
import br.com.mateus.projetoRestPuc.services.TeamService
import br.com.mateus.projetoRestPuc.services.TransferService
import br.com.mateus.projetoRestPuc.utils.Utils
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
@RequestMapping("/players")
class PlayersController(val playerService: PlayerService, val transferService: TransferService, val teamService: TeamService, val utils: Utils) {

    @ApiOperation("Return Players")
    @ApiResponses(value = [ApiResponse(code = 200, message = "Successful request"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(method = [RequestMethod.GET])
    fun findPlayers(): ResponseEntity<List<PlayerEntity>> {
        val players: List<PlayerEntity> = playerService.findPlayers()
        return ResponseEntity.ok(players)
    }

    @ApiOperation("Return Player by id")
    @ApiResponses(value = [ApiResponse(code = 200, message = "Successful request"),
        ApiResponse(code = 404, message = "Not found"),
        ApiResponse(code = 400, message = "Parameter not informed"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}"], method = [RequestMethod.GET])
    fun findPlayerId(@PathVariable id: Int?): ResponseEntity<PlayerEntity> {

        if (id == null) {
            return ResponseEntity.badRequest().build()
        }

        val player: Optional<PlayerEntity> = playerService.findPlayerById(id)
        if(player.isEmpty) {
            return ResponseEntity.notFound().build()
        }
        return ResponseEntity.ok(player.get())
    }

    @ApiOperation("Register a new Player without a Team")
    @ApiResponses(value = [ApiResponse(code = 201, message = "Player Created - New resource URI in header"),
        ApiResponse(code = 409, message = "Player already exists"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(method = [RequestMethod.POST])
    fun addPlayer(@Valid @RequestBody playerDto: PlayerDto): ResponseEntity<Response<PlayerEntity>> {
        val response: Response<PlayerEntity> = Response()

        val playerExists: Optional<PlayerEntity> = playerService.findPlayerByCode(playerDto.codePlayer!!)
        if(!playerExists.isEmpty) {
            response.erros.add("Player already exists!")
        }

        val date: Date? = utils.parseDate(playerDto.birthDate!!)

        if(date == null)
            response.erros.add("birthDate must be in format dd/MM/yyyy and be valid!")

        if(response.erros.size>0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }

        var player: PlayerEntity = playerService.convertDtoToNewPlayer(playerDto, date!!, null)
        player = playerService.persist(player)

        val uri: URI = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(player.id).toUri()

        response.data = player
        return ResponseEntity.created(uri).body(response)
    }

    @ApiOperation("Update Name of a Player")
    @ApiResponses(value = [ApiResponse(code = 204, message = "Updated Player"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
        ApiResponse(code = 404, message = "Player not exists"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}"], method = [RequestMethod.PATCH])
    fun updatePlayer(@Valid @RequestBody playerDto: PlayerUpdDto, @PathVariable id: Int?): ResponseEntity<String> {

        if (id == null) {
            return ResponseEntity.badRequest().build()
        }

        val playerExists = playerService.findPlayerById(id)
        if(playerExists.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        playerExists.get().name = playerDto.name

        playerService.persist(playerExists.get())
        return ResponseEntity.noContent().build()
    }

    @ApiOperation("Delete a Player and yours Transfers")
    @ApiResponses(value = [ApiResponse(code = 200, message = "Deleted Player and Transfers"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
        ApiResponse(code = 404, message = "Player not exists"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}"], method = [RequestMethod.DELETE])
    @Secured("ROLE_ADMIN")
    fun delPlayer(@PathVariable id: Int?): ResponseEntity<Response<Void>> {

        if (id == null) {
            return ResponseEntity.badRequest().build()
        }

        val playerExists = playerService.findPlayerById(id)
        if(playerExists.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        playerService.delete(id)
        return ResponseEntity.ok().build()
    }

    @ApiOperation("Register a new Transfer of a Player by id")
    @ApiResponses(value = [ApiResponse(code = 201, message = "Player Created - New resource URI in header"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}/transfers"], method = [RequestMethod.POST])
    fun addTransferPlayer(@PathVariable id: Int?, @Valid @RequestBody transferDto: TransferDto): ResponseEntity<Response<TransferEntity>> {
        val response: Response<TransferEntity> = Response()

        if (id == null) {
            return ResponseEntity.badRequest().build()
        }

        if(transferDto.value!! < 0) {
            response.erros.add("Value must be higher than 0!")
        }

        val player: Optional<PlayerEntity> = playerService.findPlayerById(id)
        if(player.isEmpty) {
            response.erros.add("Player not exists!")
        }

        val team: Optional<TeamEntity> = teamService.findTeamById(transferDto.destinyTeamId!!)
        if(team.isEmpty) {
            response.erros.add("Team not exists!")
        }

        if(!team.isEmpty && team.get().id == player.get().id) {
            response.erros.add("Player it's already from that team!")
        }

        val date: Date? = utils.parseDate(transferDto.transferDate!!)

        if(date == null)
            response.erros.add("transferDate must be in format dd/MM/yyyy and be valid!")


        if(response.erros.size>0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }

        var transfer: TransferEntity = transferService.convertDtoToNewTransfer(transferDto, date!!, team.get(), player.get())
        transfer = transferService.persist(transfer)
        player.get().playerTeam = team.get()
        playerService.persist(player.get())

        val uri: URI = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(transfer.id).toUri()

        response.data = transfer
        return ResponseEntity.created(uri).body(response)


    }

    @ApiOperation("Return Transfers of a Player by id")
    @ApiResponses(value = [ApiResponse(code = 200, message = "Successful request"),
        ApiResponse(code = 400, message = "Parameter not informed"),
        ApiResponse(code = 404, message = "Player not exists"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}/transfers"],method = [RequestMethod.GET])
    fun findTransfersPlayer(@PathVariable id: Int?): ResponseEntity<List<TransferEntity>> {

        if (id == null) {
            return ResponseEntity.badRequest().build()
        }

        val playerExists = playerService.findPlayerById(id)
        if(playerExists.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        val transfers: List<TransferEntity> = playerService.findPlayerTransfers(id)

        return ResponseEntity.ok(transfers)
    }

}