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
    var resultado: Int? = null,

    @get:ManyToOne(fetch = FetchType.LAZY)
    @get:JoinColumn(name = "torneioId", referencedColumnName = "id")
    var matchTournament: TournamentEntity? = null,

    @get:ManyToOne(fetch = FetchType.LAZY)
    @get:JoinColumn(name = "timeForaId", referencedColumnName = "id")
    var matchAwayTeam: TeamEntity? = null,

    @get:ManyToOne(fetch = FetchType.LAZY)
    @get:JoinColumn(name = "timeCasaId", referencedColumnName = "id")
    var matchHomeTeam: TeamEntity? = null
/*
    @get:OneToMany(mappedBy = "playerTeam")
    @JsonIgnore
    var players: List<PlayerEntity>? = null,
    @get:OneToMany(mappedBy = "originTeam", cascade = [CascadeType.REMOVE])
    @JsonIgnore
    var originTransfers: List<TransferEntity>? = null,
    @get:OneToMany(mappedBy = "destinyTeam", cascade = [CascadeType.REMOVE])
    @JsonIgnore
    var destinyTransfers: List<TransferEntity>? = null
*/

) /*{

    @PreRemove
    fun removeTeam() {
        players?.forEach { player -> player.playerTeam = null }
    }
}*/