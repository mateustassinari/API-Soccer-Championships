package br.com.mateus.projetoRestPuc.services.impl

import br.com.mateus.projetoRestPuc.dtos.TournamentDto
import br.com.mateus.projetoRestPuc.entities.TeamEntity
import br.com.mateus.projetoRestPuc.entities.TournamentEntity
import br.com.mateus.projetoRestPuc.repositories.TournamentRepository
import br.com.mateus.projetoRestPuc.response.Response
import br.com.mateus.projetoRestPuc.services.TeamService
import br.com.mateus.projetoRestPuc.services.TournamentService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.collections.ArrayList

@Service
class TournamentServiceImpl(val tournamentRepository: TournamentRepository, val teamService: TeamService): TournamentService {

    override fun findTournamentById(id: Int): Optional<TournamentEntity> = tournamentRepository.findById(id)

    override fun findTournamentByName(name: String): Optional<TournamentEntity> = tournamentRepository.findByName(name)

    override fun findTournaments(): List<TournamentEntity> = tournamentRepository.findAll()

    @Transactional
    override fun persist(tournament: TournamentEntity): TournamentEntity = tournamentRepository.save(tournament)

    @Transactional
    override fun delete(id: Int) = tournamentRepository.deleteById(id)

    override fun convertDtoToNewTournament(tournamentDto: TournamentDto, starDate: Date, endDate: Date, response: Response<TournamentEntity>): TournamentEntity {

        val teams : ArrayList<TeamEntity> = ArrayList()
        var teamExists: Optional<TeamEntity>

        tournamentDto.teamsTournament?.forEach { teamId -> teamExists = teamService.findTeamById(teamId); if(!teamExists.isEmpty) { teams.add(teamExists.get()) } else { response.erros.add("team with id ${teamId} not exists!") } }

        return TournamentEntity(null, tournamentDto.name, java.sql.Date(starDate.time), java.sql.Date(endDate.time), tournamentDto.qtdTeams, null,teams)
    }


}