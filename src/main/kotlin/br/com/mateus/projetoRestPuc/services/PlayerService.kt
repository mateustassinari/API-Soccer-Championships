package br.com.mateus.projetoRestPuc.services

import br.com.mateus.projetoRestPuc.dtos.PlayerDto
import br.com.mateus.projetoRestPuc.entities.PlayerEntity
import br.com.mateus.projetoRestPuc.entities.TeamEntity
import br.com.mateus.projetoRestPuc.entities.TransferEntity
import java.util.*

interface PlayerService {

    fun findPlayerById(id: Int): Optional<PlayerEntity>

    fun findPlayerByName(name: String): Optional<PlayerEntity>

    fun findPlayerByCpf(cpf: String): Optional<PlayerEntity>

    fun persist(player: PlayerEntity): PlayerEntity

    fun delete(id: Int)

    fun convertDtoToNewPlayer(playerDto: PlayerDto, date: Date, team: TeamEntity?): PlayerEntity

    fun findPlayerTransfers(id: Int): List<TransferEntity>

}