package br.com.mateus.projetoRestPuc.controllers

import br.com.mateus.projetoRestPuc.dtos.*
import br.com.mateus.projetoRestPuc.entities.PlayerEntity
import br.com.mateus.projetoRestPuc.entities.TeamEntity
import br.com.mateus.projetoRestPuc.entities.TransferEntity
import br.com.mateus.projetoRestPuc.response.Response
import br.com.mateus.projetoRestPuc.services.PlayerService
import br.com.mateus.projetoRestPuc.services.TeamService
import br.com.mateus.projetoRestPuc.services.TransferService
import br.com.mateus.projetoRestPuc.utils.CPFUtil
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
@RequestMapping("/players")
class PlayersController(val playerService: PlayerService, val transferService: TransferService, val teamService: TeamService) {

    @ApiOperation("Return Player by name")
    @ApiResponses(value = [ApiResponse(code = 200, message = "Successful request"),
        ApiResponse(code = 404, message = "Not found"),
        ApiResponse(code = 400, message = "Parameter not informed"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(method = [RequestMethod.GET])
    fun findPlayer(@RequestParam(value="name") name: String?): ResponseEntity<PlayerEntity> {

        if (name == null) {
            return ResponseEntity.badRequest().build()
        }

        val player: Optional<PlayerEntity> = playerService.findPlayerByName(name)
        if (player.isEmpty) {
            return ResponseEntity.notFound().build()
        }
        return ResponseEntity.ok(player.get())
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
        ApiResponse(code = 409, message = "User already registered"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(method = [RequestMethod.POST])
    fun addPlayer(@Valid @RequestBody playerDto: PlayerDto): ResponseEntity<Response<PlayerEntity>> {
        val response: Response<PlayerEntity> = Response()
        var date: Date? = null

        val playerExists: Optional<PlayerEntity> = playerService.findPlayerByCpf(playerDto.cpf!!)
        if(!playerExists.isEmpty) {
            response.erros.add("Cpf already registered!")
        }

        if(!CPFUtil.myValidateCPF(playerDto.cpf!!)) {
            response.erros.add("CPF invalid!")
        }

        try {
            val format = SimpleDateFormat("dd/MM/yyyy")
            format.isLenient = false
            date = format.parse(playerDto.birthDate)
        } catch (e: Exception) {
            response.erros.add("birthDate is must be in format dd/MM/yyyy and be valid!")
        }

        if(response.erros.size>0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }

        var player: PlayerEntity = playerService.convertDtoToNewPlayer(playerDto, date!!, null)
        player = playerService.persist(player)

        val uri: URI = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(player.id).toUri()

        response.data = player
        return ResponseEntity.created(uri).body(response)
    }

    @ApiOperation("Update a Player")
    @ApiResponses(value = [ApiResponse(code = 204, message = "Updated Player"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
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

        if(!playerDto.name.equals("") && playerDto.name != null)
            playerExists.get().name = playerDto.name

        if(!playerDto.country.equals("") && playerDto.country != null)
            playerExists.get().country = playerDto.country

        if(!playerDto.birthDate.equals("") && playerDto.birthDate != null) {

            try {
                val format = SimpleDateFormat("dd/MM/yyyy")
                format.isLenient = false
                val date = format.parse(playerDto.birthDate)
                playerExists.get().birthDate = java.sql.Date(date.time)
            } catch (e: Exception) {
                return ResponseEntity.badRequest().body("birthDate is must be in format dd/MM/yyyy and be valid!")
            }

        }

        playerService.persist(playerExists.get())
        return ResponseEntity.noContent().build()
    }

    @ApiOperation("Delete a Player")
    @ApiResponses(value = [ApiResponse(code = 200, message = "Deleted Player"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}"], method = [RequestMethod.DELETE])
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
        ApiResponse(code = 409, message = "User already registered"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}/transfers"], method = [RequestMethod.POST])
    fun addTransferPlayer(@PathVariable id: Int?, @Valid @RequestBody transferDto: TransferDto): ResponseEntity<Response<TransferEntity>> {
        val response: Response<TransferEntity> = Response()
        var date: Date? = null

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

        try {
            val format = SimpleDateFormat("dd/MM/yyyy")
            format.isLenient = false
            date = format.parse(transferDto.transferDate)
        } catch (e: Exception) {
            response.erros.add("transferDate is must be in format dd/MM/yyyy and be valid!")
        }

        if(response.erros.size>0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }

        var transfer: TransferEntity = transferService.convertDtoToNewTransfer(transferDto, date!!, team.get(), player.get())
        transfer = transferService.persist(transfer)

        val uri: URI = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(transfer.id).toUri()

        response.data = transfer
        return ResponseEntity.created(uri).body(response)


    }

    @ApiOperation("Return Transfers of a Player by id")
    @ApiResponses(value = [ApiResponse(code = 200, message = "Successful request"),
        ApiResponse(code = 404, message = "Not found"),
        ApiResponse(code = 400, message = "Parameter not informed"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}/transfers"],method = [RequestMethod.GET])
    fun findTransfersPlayer(@PathVariable id: Int?): ResponseEntity<List<PlayerTransfersDto>> {

        if (id == null) {
            return ResponseEntity.badRequest().build()
        }

        val transfers: List<PlayerTransfersDto> = playerService.findPlayerTransfers(id)

        return ResponseEntity.ok(transfers)
    }

}