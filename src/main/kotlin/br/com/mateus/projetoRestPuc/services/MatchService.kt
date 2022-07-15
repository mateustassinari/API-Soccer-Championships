package br.com.mateus.projetoRestPuc.services

import br.com.mateus.projetoRestPuc.dtos.MatchDto
import br.com.mateus.projetoRestPuc.entities.MatchEntity
import br.com.mateus.projetoRestPuc.entities.TeamEntity
import br.com.mateus.projetoRestPuc.entities.TournamentEntity
import java.util.*

interface MatchService {

    fun findMatchById(id: Int): Optional<MatchEntity>

    fun findMatches(): List<MatchEntity>

    fun persist(match: MatchEntity): MatchEntity

    fun delete(id: Int)

    fun convertDtoToNewMatch(matchDto: MatchDto, tournament: TournamentEntity?, matchAwayTeam: TeamEntity?, matchHomeTeam: TeamEntity?): MatchEntity

}