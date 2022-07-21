package br.com.mateus.projetoRestPuc.services

import br.com.mateus.projetoRestPuc.dtos.TournamentDto
import br.com.mateus.projetoRestPuc.entities.MatchEntity
import br.com.mateus.projetoRestPuc.entities.TeamEntity
import br.com.mateus.projetoRestPuc.entities.TournamentEntity
import br.com.mateus.projetoRestPuc.response.Response
import java.util.*

interface TournamentService {

    fun findTournamentById(id: Int): Optional<TournamentEntity>

    fun findTournamentByName(name: String): Optional<TournamentEntity>

    fun findTournaments(): List<TournamentEntity>

    fun persist(tournament: TournamentEntity): TournamentEntity

    fun delete(id: Int)

    fun convertDtoToNewTournament(tournamentDto: TournamentDto, starDate: Date, endDate: Date, response: Response<TournamentEntity>): TournamentEntity

    fun addTeamTournament(team : TeamEntity, tournament: TournamentEntity) : TournamentEntity

    fun deleteTeamTournament(team : TeamEntity, tournament: TournamentEntity)

    fun findTournamentMatches(id: Int): List<MatchEntity>

}