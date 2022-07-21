package br.com.mateus.projetoRestPuc.services.impl

import br.com.mateus.projetoRestPuc.dtos.TeamDto
import br.com.mateus.projetoRestPuc.dtos.TeamTournamentsDto
import br.com.mateus.projetoRestPuc.dtos.TeamTransfersDto
import br.com.mateus.projetoRestPuc.entities.MatchEntity
import br.com.mateus.projetoRestPuc.entities.PlayerEntity
import br.com.mateus.projetoRestPuc.entities.TeamEntity
import br.com.mateus.projetoRestPuc.repositories.TeamRepository
import br.com.mateus.projetoRestPuc.services.TeamService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.collections.ArrayList


@Service
class TeamServiceImpl(val teamRepository: TeamRepository): TeamService {

    override fun findTeamById(id: Int): Optional<TeamEntity> = teamRepository.findById(id)

    override fun findTeams(): List<TeamEntity> = teamRepository.findAll()

    override fun findTeamByNameAndUfAndCity(name: String, uf: String, city: String): Optional<TeamEntity> = teamRepository.findByNameAndUfAndCity(name,uf,city)

    @Transactional
    override fun persist(team: TeamEntity): TeamEntity = teamRepository.save(team)

    @Transactional
    override fun delete(id: Int): Unit = teamRepository.deleteById(id)

    override fun convertDtoToNewTeam(teamDto: TeamDto, date: Date): TeamEntity {
        return TeamEntity(null, teamDto.name, teamDto.uf, teamDto.city, java.sql.Date(date.time), null, null, null)
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

    override fun findTeamTournaments(id: Int): List<TeamTournamentsDto> {
        val listTeamTournaments = ArrayList<TeamTournamentsDto>()
        var teamTournamentsDto : TeamTournamentsDto
        val team = teamRepository.findById(id)
        if(team.isEmpty) {
            return listTeamTournaments
        }


        team.get().tournamentsTeam?.forEach { tournament ->
            teamTournamentsDto = TeamTournamentsDto()
            teamTournamentsDto.id = tournament.id;
            teamTournamentsDto.name = tournament.name;
            teamTournamentsDto.startDate = tournament.startDate;
            teamTournamentsDto.endDate = tournament.endDate;
            teamTournamentsDto.qtdTeams = tournament.qtdTeams;
            listTeamTournaments.add(teamTournamentsDto)
        }

        return listTeamTournaments

    }

    override fun findMatchesTeam(id: Int, type: String): List<MatchEntity> {
        val team = teamRepository.findById(id)
        if(team.isEmpty || (type == "home" && team.get().homeMatches == null) || (type == "away" && team.get().awayMatches == null)) {
            return arrayListOf()
        }

        return if(type == "home") { team.get().homeMatches!! } else { team.get().awayMatches!! }

    }

}