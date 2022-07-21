package br.com.mateus.projetoRestPuc.dtos

import org.hibernate.validator.constraints.NotEmpty

data class TournamentUpdDto (

    @get:NotEmpty(message="Name cannot be empty")
    var name: String? = null,

    @get:NotEmpty(message="StartDate cannot be empty")
    var startDate: String? = null,

    @get:NotEmpty(message="EndDate cannot be empty")
    var endDate: String? = null

)