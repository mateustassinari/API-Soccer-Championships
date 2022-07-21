package br.com.mateus.projetoRestPuc.repositories

import br.com.mateus.projetoRestPuc.entities.MatchEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MatchRepository: JpaRepository<MatchEntity, Int> {
}