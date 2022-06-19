package br.com.mateus.projetoRestPuc.dtos

data class PlayerTransfersDto (

    var id: Int? = null,

    var value: Double? = null,

    var transferDate: String? = null,

    var originTeam: String? = null,

    var destinyTeam: String? = null,

    var player: String? = null

)