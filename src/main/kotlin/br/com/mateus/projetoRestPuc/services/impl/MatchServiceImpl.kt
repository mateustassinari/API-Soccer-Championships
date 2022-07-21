package br.com.mateus.projetoRestPuc.services.impl

import br.com.mateus.projetoRestPuc.dtos.MatchDto
import br.com.mateus.projetoRestPuc.entities.MatchEntity
import br.com.mateus.projetoRestPuc.entities.TeamEntity
import br.com.mateus.projetoRestPuc.entities.TournamentEntity
import br.com.mateus.projetoRestPuc.repositories.MatchRepository
import br.com.mateus.projetoRestPuc.services.MatchService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class MatchServiceImpl(val matchRepository: MatchRepository): MatchService {

    override fun findMatchById(id: Int): Optional<MatchEntity> = matchRepository.findById(id)

    @Transactional
    override fun persist(match: MatchEntity): MatchEntity = matchRepository.save(match)

    @Transactional
    override fun delete(id: Int) = matchRepository.deleteById(id)

    override fun convertDtoToNewMatch(matchDto: MatchDto, tournament: TournamentEntity?, matchAwayTeam: TeamEntity?, matchHomeTeam: TeamEntity?): MatchEntity {
        return MatchEntity(null, matchDto.result, tournament,matchAwayTeam,matchHomeTeam)
    }

}