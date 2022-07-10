package br.com.mateus.projetoRestPuc.repositories

import br.com.mateus.projetoRestPuc.entities.TournamentEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TournamentRepository: JpaRepository<TournamentEntity,Int> {

    fun findByName(name: String): Optional<TournamentEntity>

}