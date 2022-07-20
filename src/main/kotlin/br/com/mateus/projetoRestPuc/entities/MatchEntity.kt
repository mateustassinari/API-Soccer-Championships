package br.com.mateus.projetoRestPuc.entities

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.annotations.ApiModelProperty
import java.sql.Date
import javax.persistence.*

@Entity
@Table(name = "Partidas", catalog = "")
data class MatchEntity (
    @get:Id
    @get:GeneratedValue(strategy=GenerationType.IDENTITY)
    @get:Column(name = "id", nullable = false, insertable = false, updatable = false)
    var id: Int? = null,
    @get:Basic
    @get:Column(name = "resultado", nullable = false)
    var result: String? = null,

    @get:ManyToOne(fetch = FetchType.LAZY)
    @get:JoinColumn(name = "torneioId", referencedColumnName = "id")
    @JsonIgnore
    var matchTournament: TournamentEntity? = null,

    @get:ManyToOne(fetch = FetchType.LAZY)
    @get:JoinColumn(name = "timeCasaId", referencedColumnName = "id")
    var matchHomeTeam: TeamEntity? = null,

    @get:ManyToOne(fetch = FetchType.LAZY)
    @get:JoinColumn(name = "timeForaId", referencedColumnName = "id")
    var matchAwayTeam: TeamEntity? = null,

    @get:OneToMany(mappedBy = "matchEvent")
    @JsonIgnore
    var events: List<EventEntity>? = null

)