package br.com.mateus.projetoRestPuc.entities

import com.fasterxml.jackson.annotation.*
import io.swagger.annotations.ApiModelProperty
import java.sql.Date
import javax.persistence.*

@Entity
@Table(name = "Transferencias", catalog = "")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
data class TransferEntity (
    @get:Id
    @get:GeneratedValue(strategy= GenerationType.IDENTITY)
    @get:Column(name = "id", nullable = false, insertable = false, updatable = false)
    var id: Int? = null,
    @get:Basic
    @get:Column(name = "valor", nullable = false)
    var value: Double? = null,
    @get:Basic
    @get:Column(name = "dataTransferencia", nullable = false)
    @JsonFormat(pattern="dd/MM/yyyy")
    @ApiModelProperty(dataType = "java.sql.Date")
    var transferDate: Date? = null,

    @get:ManyToOne(fetch = FetchType.LAZY)
    @get:JoinColumn(name = "timeOrigemId", referencedColumnName = "id",nullable=false)
    var originTeam: TeamEntity? = null,
    @get:ManyToOne(fetch = FetchType.LAZY)
    @get:JoinColumn(name = "timeDestinoId", referencedColumnName = "id",nullable=false)
    var destinyTeam: TeamEntity? = null,
    @get:ManyToOne(fetch = FetchType.LAZY)
    @get:JoinColumn(name = "jogadorId", referencedColumnName = "id",nullable=false)
    var player: PlayerEntity? = null,

    @get:Column(name = "timeOrigemHistoricoId")
    @JsonIgnore
    var originTeamHistoryId: Int? = null,
    @get:Column(name = "timeDestinoHistoricoId")
    @JsonIgnore
    var destinyTeamHistoryId: Int? = null,
    @get:Column(name = "jogadorHistoricoId")
    @JsonIgnore
    var playerHistoryId: Int? = null

)