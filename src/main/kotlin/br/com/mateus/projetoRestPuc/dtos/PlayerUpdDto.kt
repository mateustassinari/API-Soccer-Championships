package br.com.mateus.projetoRestPuc.dtos

import org.hibernate.validator.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class PlayerUpdDto (

    @get:NotEmpty(message="Name cannot be empty")
    @get:NotNull(message="Name cannot be null")
    var name: String? = null

)