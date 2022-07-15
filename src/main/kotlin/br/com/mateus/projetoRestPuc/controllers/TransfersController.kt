package br.com.mateus.projetoRestPuc.controllers

import br.com.mateus.projetoRestPuc.dtos.*
import br.com.mateus.projetoRestPuc.entities.TransferEntity
import br.com.mateus.projetoRestPuc.services.TransferService
import br.com.mateus.projetoRestPuc.utils.Utils
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.text.SimpleDateFormat
import java.util.*
import javax.validation.Valid


@RestController
@RequestMapping("/transfers")
class TransfersController(val transferService: TransferService, val utils: Utils) {

    @ApiOperation("Return Transfer by id")
    @ApiResponses(value = [ApiResponse(code = 200, message = "Successful request"),
        ApiResponse(code = 404, message = "Not found"),
        ApiResponse(code = 400, message = "Parameter not informed"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}"], method = [RequestMethod.GET])
    fun findTransferId(@PathVariable id: Int?): ResponseEntity<TransferEntity> {

        if (id == null) {
            return ResponseEntity.badRequest().build()
        }

        val transfer: Optional<TransferEntity> = transferService.findTransferById(id)
        if(transfer.isEmpty) {
            return ResponseEntity.notFound().build()
        }
        return ResponseEntity.ok(transfer.get())
    }


    @ApiOperation("Update a Transfer")
    @ApiResponses(value = [ApiResponse(code = 204, message = "Updated Transfer"),
        ApiResponse(code = 400, message = "Lack of information/poorly formatted request"),
        ApiResponse(code = 404, message = "Transfer not exists"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    @RequestMapping(value = ["/{id}"], method = [RequestMethod.PATCH])
    fun updateTransfer(@Valid @RequestBody transferDto: TransferUpdDto, @PathVariable id: Int?): ResponseEntity<String> {

        if (id == null) {
            return ResponseEntity.badRequest().build()
        }

        val transferExists = transferService.findTransferById(id)
        if(transferExists.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        if(transferDto.value != null) {
            if (transferDto.value!! < 0) {
                return ResponseEntity.badRequest().body("Value must be higher than 0!")
            }
            transferExists.get().value = transferDto.value
        }

        if(!transferDto.transferDate.equals("") && transferDto.transferDate != null) {

            val date = utils.parseDate(transferDto.transferDate!!)

            if(date != null) {
                transferExists.get().transferDate = java.sql.Date(date.time)
            } else {
                return ResponseEntity.badRequest().body("transferDate must be in format dd/MM/yyyy and be valid!")
            }

        }

        transferService.persist(transferExists.get())
        return ResponseEntity.noContent().build()
    }

}