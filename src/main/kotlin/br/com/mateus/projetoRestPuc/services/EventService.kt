package br.com.mateus.projetoRestPuc.services

import br.com.mateus.projetoRestPuc.entities.EventEntity
import java.util.Optional

interface EventService {

    fun findEventById(id: Int): Optional<EventEntity>

    fun persist(event: EventEntity): EventEntity
}