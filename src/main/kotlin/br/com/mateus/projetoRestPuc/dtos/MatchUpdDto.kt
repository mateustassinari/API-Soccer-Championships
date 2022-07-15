package br.com.mateus.projetoRestPuc.dtos

import org.hibernate.validator.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class MatchUpdDto (

    @get:NotEmpty(message="Result cannot be empty")
    @get:NotNull(message="Result cannot be null")
    var result: String? = null

)