package br.com.mateus.projetoRestPuc.entities

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.swagger.annotations.ApiModelProperty
import java.sql.Date
import javax.persistence.*

@Entity
@Table(name = "Torneios", catalog = "")
data class TournamentEntity (
    @get:Id
    @get:GeneratedValue(strategy=GenerationType.IDENTITY)
    @get:Column(name = "id", nullable = false, insertable = false, updatable = false)
    var id: Int? = null,
    @get:Basic
    @get:Column(name = "nome", nullable = false)
    var name: String? = null,
    @get:Basic
    @get:Column(name = "dataInicio", nullable = false)
    @JsonFormat(pattern="dd/MM/yyyy")
    @ApiModelProperty(dataType = "java.sql.Date")
    var startDate: Date? = null,
    @get:Column(name = "dataFim", nullable = false)
    @JsonFormat(pattern="dd/MM/yyyy")
    @ApiModelProperty(dataType = "java.sql.Date")
    var endDate: Date? = null,
    @get:Basic
    @get:Column(name = "qtdTime", nullable = false)
    var qtdTime: Int? = null,

    @get:OneToMany(mappedBy = "matchTournament")
    @JsonIgnore
    var matches: List<MatchEntity>? = null,

    @get:ManyToMany(mappedBy="tournamentTeams")
    var teamsTournament: List<TeamEntity>? = null


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

