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
    var qtdTeams: Int? = null,

    @get:OneToMany(mappedBy = "matchTournament")
    @JsonIgnore
    var matches: List<MatchEntity>? = null,

    @get:ManyToMany(cascade = [CascadeType.MERGE, CascadeType.PERSIST])
    @get:JoinTable(
        name = "TimesTorneio",
        joinColumns = [JoinColumn(name = "torneioId", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "timeId", referencedColumnName = "id")]
    )
    var teamsTournament: MutableList<TeamEntity>? = null

) {
    fun removeTournamentsTeam(teamToDelete : TeamEntity) {
        teamsTournament = teamsTournament?.filter { team -> team.id != teamToDelete.id }?.toMutableList()
        teamToDelete.tournamentsTeam = teamToDelete.tournamentsTeam?.filter { tournament -> tournament.id != this.id }?.toMutableList()
    }
}

