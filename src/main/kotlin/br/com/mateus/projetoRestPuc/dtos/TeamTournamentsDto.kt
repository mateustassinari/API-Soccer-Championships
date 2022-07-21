package br.com.mateus.projetoRestPuc.dtos

import com.fasterxml.jackson.annotation.JsonFormat
import java.util.*

data class TeamTournamentsDto (

    var id: Int? = null,
    var name: String? = null,
    @JsonFormat(pattern="dd/MM/yyyy")
    var startDate: Date? = null,
    @JsonFormat(pattern="dd/MM/yyyy")
    var endDate: Date? = null,
    var qtdTeams: Int? = null

)