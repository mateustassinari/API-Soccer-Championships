package br.com.mateus.projetoRestPuc.dtos

import org.hibernate.validator.constraints.NotEmpty

data class TransferUpdDto (

    var value: Double? = null,

    var transferDate: String? = null

)