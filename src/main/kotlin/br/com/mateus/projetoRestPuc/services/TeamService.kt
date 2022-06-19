package br.com.mateus.projetoRestPuc.services

import br.com.mateus.projetoRestPuc.dtos.TeamDto
import br.com.mateus.projetoRestPuc.entities.TeamEntity
import java.util.*

interface TeamService {

    fun findTeamById(id: Int): Optional<TeamEntity>

    fun findTeamByName(name: String): Optional<TeamEntity>

    fun persist(team: TeamEntity): TeamEntity

    fun delete(id: Int)

    fun convertDtoToNewTeam(teamDto: TeamDto): TeamEntity?

}