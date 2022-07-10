package br.com.mateus.projetoRestPuc.dtos

import org.hibernate.validator.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class PlayerDto (

    @get:NotEmpty(message="Name cannot be empty")
    @get:NotNull(message="Name cannot be null")
    var name: String? = null,

    @get:NotEmpty(message="Country cannot be empty")
    @get:NotNull(message="Country cannot be null")
    var country: String? = null,

    @get:NotEmpty(message="CodePlayer cannot be empty")
    @get:NotNull(message="CodePlayer cannot be null")
    var codePlayer: String? = null,

    @get:NotEmpty(message="BirthDate cannot be empty")
    @get:NotNull(message="BirthDate cannot be null")
    var birthDate: String? = null

)