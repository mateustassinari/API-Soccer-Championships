package br.com.mateus.projetoRestPuc.dtos

import br.com.mateus.projetoRestPuc.entities.TransferEntity


data class TeamTransfersDto (

    var destinyTransfers: List<TransferEntity>? = null,
    var originTransfers: List<TransferEntity>? = null

)