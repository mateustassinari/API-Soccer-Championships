package br.com.mateus.projetoRestPuc.repositories

import br.com.mateus.projetoRestPuc.entities.TransferEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TransferRepository: JpaRepository<TransferEntity, Int> {
}