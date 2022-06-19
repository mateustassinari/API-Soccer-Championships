package br.com.mateus.projetoRestPuc.controllers

import br.com.mateus.projetoRestPuc.dtos.*
import br.com.mateus.projetoRestPuc.entities.TransferEntity
import br.com.mateus.projetoRestPuc.services.TransferService
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
class TransfersController(val transferService: TransferService) {

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

            try {
                val format = SimpleDateFormat("dd/MM/yyyy")
                format.isLenient = false
                val date = format.parse(transferDto.transferDate)
                transferExists.get().transferDate = java.sql.Date(date.time)
            } catch (e: Exception) {
                return ResponseEntity.badRequest().body("transferDate is must be in format dd/MM/yyyy and be valid!")
            }

        }

        transferService.persist(transferExists.get())
        return ResponseEntity.noContent().build()
    }

}