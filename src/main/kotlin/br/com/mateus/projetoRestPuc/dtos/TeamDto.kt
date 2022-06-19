package br.com.mateus.projetoRestPuc.dtos

import org.hibernate.validator.constraints.NotEmpty

data class TeamDto (

        @get:NotEmpty(message="Name cannot be empty")
        var name: String? = null,

        @get:NotEmpty(message="Place cannot be empty")
        var place: String? = null,

        @get:NotEmpty(message="FoundingDate cannot be empty")
        var foundingDate: String? = null

)