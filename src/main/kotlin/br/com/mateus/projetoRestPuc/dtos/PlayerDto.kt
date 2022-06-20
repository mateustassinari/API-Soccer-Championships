package br.com.mateus.projetoRestPuc.dtos

import org.hibernate.validator.constraints.NotEmpty

data class PlayerDto (

    @get:NotEmpty(message="Name cannot be empty")
    var name: String? = null,

    @get:NotEmpty(message="Country cannot be empty")
    var country: String? = null,

    @get:NotEmpty(message="CodePlayer cannot be empty")
    var codePlayer: String? = null,

    @get:NotEmpty(message="BirthDate cannot be empty")
    var birthDate: String? = null

)