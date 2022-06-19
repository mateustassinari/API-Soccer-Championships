package br.com.mateus.projetoRestPuc.services.impl

import br.com.mateus.projetoRestPuc.dtos.TeamDto
import br.com.mateus.projetoRestPuc.entities.TeamEntity
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

    override fun convertDtoToNewTeam(teamDto: TeamDto): TeamEntity? {
        return try {
            val format = SimpleDateFormat("dd/MM/yyyy")
            format.isLenient = false
            val date = format.parse(teamDto.foundingDate)
            TeamEntity(null, teamDto.name, teamDto.place, java.sql.Date(date.time), null, null, null)
        } catch (e: Exception) {
            null
        }

    }

}