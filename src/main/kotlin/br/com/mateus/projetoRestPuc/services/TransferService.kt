package br.com.mateus.projetoRestPuc.services

import br.com.mateus.projetoRestPuc.dtos.TransferDto
import br.com.mateus.projetoRestPuc.entities.PlayerEntity
import br.com.mateus.projetoRestPuc.entities.TeamEntity
import br.com.mateus.projetoRestPuc.entities.TransferEntity
import java.util.*

interface TransferService {

    fun findTransferById(id: Int): Optional<TransferEntity>

    fun persist(transfer: TransferEntity): TransferEntity

    fun convertDtoToNewTransfer(transferDto: TransferDto, date: Date, destinyTeam: TeamEntity, player: PlayerEntity): TransferEntity

}