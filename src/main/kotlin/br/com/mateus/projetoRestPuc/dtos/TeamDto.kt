package br.com.mateus.projetoRestPuc.dtos

import org.hibernate.validator.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class TeamDto (

        @get:NotEmpty(message="Name cannot be empty")
        @get:NotNull(message="Name cannot be null")
        var name: String? = null,

        @get:NotEmpty(message="Uf cannot be empty")
        @get:NotNull(message="Uf cannot be null")
        var uf: String? = null,

        @get:NotEmpty(message="City cannot be empty")
        @get:NotNull(message="City cannot be null")
        var city: String? = null,

        @get:NotEmpty(message="FoundingDate cannot be empty")
        @get:NotNull(message="FoundingDate cannot be null")
        var foundingDate: String? = null

)