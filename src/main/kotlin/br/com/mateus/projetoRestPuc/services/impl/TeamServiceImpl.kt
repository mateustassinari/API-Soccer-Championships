package br.com.mateus.projetoRestPuc.services.impl

import br.com.mateus.projetoRestPuc.dtos.TeamDto
import br.com.mateus.projetoRestPuc.dtos.TeamTransfersDto
import br.com.mateus.projetoRestPuc.entities.PlayerEntity
import br.com.mateus.projetoRestPuc.entities.TeamEntity
import br.com.mateus.projetoRestPuc.entities.TransferEntity
import br.com.mateus.projetoRestPuc.repositories.TeamRepository
import br.com.mateus.projetoRestPuc.services.TeamService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.text.SimpleDateFormat
import java.util.*


@Service
class TeamServiceImpl(val teamRepository: TeamRepository): TeamService {

    override fun findTeamById(id: Int): Optional<TeamEntity> = teamRepository.findById(id)

    override fun findTeamByName(name: String): Optional<TeamEntity> = teamRepository.findByName(name)

    @Transactional
    override fun persist(team: TeamEntity): TeamEntity = teamRepository.save(team)

    @Transactional
    override fun delete(id: Int): Unit = teamRepository.deleteById(id)

    override fun convertDtoToNewTeam(teamDto: TeamDto, date: Date): TeamEntity {
        return TeamEntity(null, teamDto.name, teamDto.place, java.sql.Date(date.time), null, null, null)
    }

    override fun findTeamPlayers(id: Int): List<PlayerEntity> {
        val team = teamRepository.findById(id)
        if(team.isEmpty || team.get().players == null) {
            return arrayListOf()
        }

        return team.get().players!!

    }

    override fun findTeamTransfers(id: Int): TeamTransfersDto {
        val teamTransfersDto = TeamTransfersDto()
        val team = teamRepository.findById(id)
        if(team.isEmpty) {
            return teamTransfersDto
        }

        teamTransfersDto.destinyTransfers = team.get().destinyTransfers
        teamTransfersDto.originTransfers = team.get().originTransfers

        return teamTransfersDto

    }

}