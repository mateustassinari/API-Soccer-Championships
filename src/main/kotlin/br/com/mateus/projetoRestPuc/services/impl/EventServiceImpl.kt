package br.com.mateus.projetoRestPuc.services.impl

import br.com.mateus.projetoRestPuc.entities.*
import br.com.mateus.projetoRestPuc.repositories.EventRepository
import br.com.mateus.projetoRestPuc.services.EventService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class EventServiceImpl(val eventRepository: EventRepository): EventService {

    override fun findEventById(id: Int): Optional<EventEntity> = eventRepository.findById(id)

    @Transactional
    override fun persist(event: EventEntity): EventEntity = eventRepository.save(event)

}