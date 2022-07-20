package br.com.mateus.projetoRestPuc.services.impl

import br.com.mateus.projetoRestPuc.dtos.PlayerDto
import br.com.mateus.projetoRestPuc.entities.*
import br.com.mateus.projetoRestPuc.repositories.EventRepository
import br.com.mateus.projetoRestPuc.repositories.PlayerRepository
import br.com.mateus.projetoRestPuc.repositories.TeamRepository
import br.com.mateus.projetoRestPuc.services.EventService
import br.com.mateus.projetoRestPuc.services.PlayerService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Date
import java.util.*

@Service
class EventServiceImpl(val eventRepository: EventRepository): EventService {

    @Transactional
    override fun persist(event: EventEntity): EventEntity = eventRepository.save(event)

}