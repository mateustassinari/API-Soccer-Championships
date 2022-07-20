package br.com.mateus.projetoRestPuc.repositories

import br.com.mateus.projetoRestPuc.entities.EventEntity
import org.springframework.data.jpa.repository.JpaRepository

interface EventRepository: JpaRepository<EventEntity, Int>  {
}