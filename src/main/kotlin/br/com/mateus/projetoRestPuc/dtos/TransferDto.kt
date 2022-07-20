package br.com.mateus.projetoRestPuc.dtos

import org.hibernate.validator.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class TransferDto (

    @get:NotNull(message="Value cannot be null")
    var value: Double? = null,

    @get:NotEmpty(message="TransferDate cannot be empty")
    @get:NotNull(message="TransferDate cannot be null")
    var transferDate: String? = null,

    @get:NotNull(message="TeamDestinyId cannot be empty")
    var destinyTeamId: Int? = null

)