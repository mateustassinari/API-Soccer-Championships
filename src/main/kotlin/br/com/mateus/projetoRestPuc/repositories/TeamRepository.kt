package br.com.mateus.projetoRestPuc.repositories

import br.com.mateus.projetoRestPuc.entities.TeamEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TeamRepository: JpaRepository<TeamEntity, Int> {

    fun findByName(name: String): Optional<TeamEntity>

}