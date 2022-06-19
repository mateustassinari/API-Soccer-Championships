package br.com.mateus.projetoRestPuc.dtos

import org.hibernate.validator.constraints.NotEmpty

data class PlayerDto (

    @get:NotEmpty(message="Name cannot be empty")
    var name: String? = null,

    @get:NotEmpty(message="Country cannot be empty")
    var country: String? = null,

    @get:NotEmpty(message="Cpf cannot be empty")
    var cpf: String? = null,

    @get:NotEmpty(message="BirthDate cannot be empty")
    var birthDate: String? = null

)