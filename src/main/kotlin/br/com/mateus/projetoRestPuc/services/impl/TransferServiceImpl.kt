package br.com.mateus.projetoRestPuc.services.impl

import br.com.mateus.projetoRestPuc.dtos.TransferDto
import br.com.mateus.projetoRestPuc.entities.PlayerEntity
import br.com.mateus.projetoRestPuc.entities.TeamEntity
import br.com.mateus.projetoRestPuc.entities.TransferEntity
import br.com.mateus.projetoRestPuc.repositories.TransferRepository
import br.com.mateus.projetoRestPuc.services.TransferService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*

@Service
class TransferServiceImpl(val transferRepository: TransferRepository): TransferService {

    override fun findTransferById(id: Int): Optional<TransferEntity> = transferRepository.findById(id)

    @Transactional
    override fun persist(transfer: TransferEntity): TransferEntity = transferRepository.save(transfer)

    override fun convertDtoToNewTransfer(transferDto: TransferDto, date: java.util.Date, destinyTeam: TeamEntity, player: PlayerEntity): TransferEntity {
        return TransferEntity(null, transferDto.value, Date(date.time), player.playerTeam, destinyTeam,player)
    }

}