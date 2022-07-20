package br.com.mateus.projetoRestPuc.entities

import javax.persistence.*

@Entity
@Table(name = "Eventos", catalog = "")
data class EventEntity (
    @get:Id
    @get:GeneratedValue(strategy=GenerationType.IDENTITY)
    @get:Column(name = "id", nullable = false, insertable = false, updatable = false)
    var id: Int? = null,

    @get:Basic
    @get:Column(name = "mensagem", nullable = false)
    var message: String? = null,

    @get:ManyToOne(fetch = FetchType.LAZY)
    @get:JoinColumn(name = "partidaId", referencedColumnName = "id")
    var matchEvent: MatchEntity? = null,

    @get:Enumerated(EnumType.STRING)
    @get:Column(name = "tipo", nullable = false)
    var type: TypeEventEnum? = null,

)