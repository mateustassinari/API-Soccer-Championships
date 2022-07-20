package br.com.mateus.projetoRestPuc.services

import br.com.mateus.projetoRestPuc.dtos.PlayerDto
import br.com.mateus.projetoRestPuc.entities.*
import java.util.*

interface EventService {

    fun persist(event: EventEntity): EventEntity
}