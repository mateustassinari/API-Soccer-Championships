package br.com.mateus.projetoRestPuc.services.impl

import br.com.mateus.projetoRestPuc.dtos.PlayerDto
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

@Service
class PlayerServiceImpl(val playerRepository: PlayerRepository, val teamRepository: TeamRepository): PlayerService {

    override fun findPlayerById(id: Int): Optional<PlayerEntity> = playerRepository.findById(id)

    override fun findPlayers(): List<PlayerEntity> = playerRepository.findAll()

    override fun findPlayerByCode(codePlayer: String): Optional<PlayerEntity> = playerRepository.findByCodePlayer(codePlayer)

    @Transactional
    override fun persist(player: PlayerEntity): PlayerEntity = playerRepository.save(player)

    @Transactional
    override fun delete(id: Int): Unit = playerRepository.deleteById(id)

    override fun convertDtoToNewPlayer(playerDto: PlayerDto, date: java.util.Date, team: TeamEntity?): PlayerEntity {
        return PlayerEntity(null, playerDto.name, playerDto.country, Date(date.time), playerDto.codePlayer, team)
    }

    override fun findPlayerTransfers(id: Int): List<TransferEntity> {
        val player = playerRepository.findById(id)
        if(player.isEmpty || player.get().transfers == null) {
            return arrayListOf()
        }

        return player.get().transfers!!

    }

}