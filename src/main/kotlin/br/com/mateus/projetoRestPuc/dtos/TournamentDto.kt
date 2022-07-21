package br.com.mateus.projetoRestPuc.dtos

import org.hibernate.validator.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class TournamentDto (

    @get:NotEmpty(message="Name cannot be empty")
    @get:NotNull(message="Name cannot be null")
    var name: String? = null,

    @get:NotEmpty(message="StartDate cannot be empty")
    @get:NotNull(message="StartDate cannot be null")
    var startDate: String? = null,

    @get:NotEmpty(message="EndDate cannot be empty")
    @get:NotNull(message="EndDate cannot be null")
    var endDate: String? = null,

    @get:NotNull(message="QtdTeams cannot be null")
    var qtdTeams: Int? = null,

    @get:NotNull(message="TeamsTournament cannot be null")
    var teamsTournament: List<Int>? = null

)