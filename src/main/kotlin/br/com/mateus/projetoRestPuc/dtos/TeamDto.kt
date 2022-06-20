package br.com.mateus.projetoRestPuc.dtos

import org.hibernate.validator.constraints.NotEmpty

data class TeamDto (

        @get:NotEmpty(message="Name cannot be empty")
        var name: String? = null,

        @get:NotEmpty(message="Uf cannot be empty")
        var uf: String? = null,

        @get:NotEmpty(message="City cannot be empty")
        var city: String? = null,

        @get:NotEmpty(message="FoundingDate cannot be empty")
        var foundingDate: String? = null

)