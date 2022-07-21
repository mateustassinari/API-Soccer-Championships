package br.com.mateus.projetoRestPuc.dtos

import org.hibernate.validator.constraints.NotEmpty

data class LoginDto (

    @get:NotEmpty(message="Mandatory")
    var login: String? = null,

    @get:NotEmpty(message="Mandatory")
    var password: String? = null

)