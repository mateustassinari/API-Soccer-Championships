package br.com.mateus.projetoRestPuc.repositories

import br.com.mateus.projetoRestPuc.entities.PlayerEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PlayerRepository: JpaRepository<PlayerEntity, Int>  {

    fun findByName(name: String): Optional<PlayerEntity>

    fun findByCpf(cpf: String): Optional<PlayerEntity>

}