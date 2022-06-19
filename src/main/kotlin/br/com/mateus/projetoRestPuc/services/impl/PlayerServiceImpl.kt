package br.com.mateus.projetoRestPuc.services.impl

import br.com.mateus.projetoRestPuc.dtos.PlayerDto
import br.com.mateus.projetoRestPuc.dtos.PlayerTransfersDto
import br.com.mateus.projetoRestPuc.entities.PlayerEntity
import br.com.mateus.projetoRestPuc.entities.TeamEntity
import br.com.mateus.projetoRestPuc.entities.TransferEntity
import br.com.mateus.projetoRestPuc.repositories.PlayerRepository
import br.com.mateus.projetoRestPuc.repositories.TeamRepository
import br.com.mateus.projetoRestPuc.services.PlayerService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Date
import java.util.*
import kotlin.collections.ArrayList

@Service
class PlayerServiceImpl(val playerRepository: PlayerRepository, val teamRepository: TeamRepository): PlayerService {

    override fun findPlayerById(id: Int): Optional<PlayerEntity> = playerRepository.findById(id)

    override fun findPlayerByName(name: String): Optional<PlayerEntity> = playerRepository.findByName(name)

    override fun findPlayerByCpf(cpf: String): Optional<PlayerEntity> = playerRepository.findByCpf(cpf)

    @Transactional
    override fun persist(player: PlayerEntity): PlayerEntity = playerRepository.save(player)

    @Transactional
    override fun delete(id: Int): Unit = playerRepository.deleteById(id)

    override fun convertDtoToNewPlayer(playerDto: PlayerDto, date: java.util.Date, team: TeamEntity?): PlayerEntity {
        return PlayerEntity(null, playerDto.name, playerDto.country, Date(date.time), playerDto.cpf, team)
    }

    override fun findPlayerTransfers(id: Int): List<PlayerTransfersDto> {
        var playerTransfersDto: PlayerTransfersDto
        val listPlayerTransfers = arrayListOf<PlayerTransfersDto>()
        val player = playerRepository.findById(id)
        if(player.isEmpty || player.get().transfers == null) {
            return arrayListOf()
        }

        for (transfer: TransferEntity in player.get().transfers!!) {

            playerTransfersDto = PlayerTransfersDto()
            playerTransfersDto.id = transfer.id
            playerTransfersDto.value = transfer.value
            playerTransfersDto.transferDate = transfer.transferDate.toString()
            playerTransfersDto.originTeam = transfer.originTeam?.name
            playerTransfersDto.destinyTeam = transfer.destinyTeam?.name
            playerTransfersDto.player = transfer.player?.name
            listPlayerTransfers.add(playerTransfersDto)

        }

        return listPlayerTransfers

    }

}