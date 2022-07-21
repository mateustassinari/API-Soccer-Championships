package br.com.mateus.projetoRestPuc.dtos

import org.hibernate.validator.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class MatchDto (

    @get:NotEmpty(message="Result cannot be empty")
    @get:NotNull(message="Result cannot be null")
    var result: String? = null,

    @get:NotNull(message="MatchHomeTeam cannot be null")
    var matchHomeTeam: Int? = null,

    @get:NotNull(message="MatchAwayTeam cannot be null")
    var matchAwayTeam: Int? = null

)