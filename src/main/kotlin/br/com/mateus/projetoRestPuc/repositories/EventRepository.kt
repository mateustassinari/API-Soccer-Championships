package br.com.mateus.projetoRestPuc.repositories

import br.com.mateus.projetoRestPuc.entities.EventEntity
import br.com.mateus.projetoRestPuc.entities.PlayerEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface EventRepository: JpaRepository<EventEntity, Int>  {
}