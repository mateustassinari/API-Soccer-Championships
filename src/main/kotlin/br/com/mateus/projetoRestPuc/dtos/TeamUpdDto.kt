package br.com.mateus.projetoRestPuc.dtos

import org.hibernate.validator.constraints.NotEmpty

data class TeamUpdDto (

    @get:NotEmpty(message="Name cannot be empty")
    var name: String? = null

)