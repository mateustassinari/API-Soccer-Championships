package br.com.mateus.projetoRestPuc.services

import br.com.mateus.projetoRestPuc.dtos.TeamDto
import br.com.mateus.projetoRestPuc.dtos.TeamTournamentsDto
import br.com.mateus.projetoRestPuc.dtos.TeamTransfersDto
import br.com.mateus.projetoRestPuc.entities.MatchEntity
import br.com.mateus.projetoRestPuc.entities.PlayerEntity
import br.com.mateus.projetoRestPuc.entities.TeamEntity
import br.com.mateus.projetoRestPuc.entities.TransferEntity
import java.util.*

interface TeamService {

    fun findTeamById(id: Int): Optional<TeamEntity>

    fun findTeams(): List<TeamEntity>

    fun findTeamByNameAndUfAndCity(name: String, uf: String, city: String): Optional<TeamEntity>

    fun persist(team: TeamEntity): TeamEntity

    fun delete(id: Int)

    fun convertDtoToNewTeam(teamDto: TeamDto, date: Date): TeamEntity

    fun findTeamPlayers(id: Int): List<PlayerEntity>

    fun findTeamTransfers(id: Int): TeamTransfersDto

    fun findTeamTournaments(id: Int): List<TeamTournamentsDto>

    fun findMatchesTeam(id: Int, type: String): List<MatchEntity>

}