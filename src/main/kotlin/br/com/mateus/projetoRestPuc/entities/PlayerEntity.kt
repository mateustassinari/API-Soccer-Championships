package br.com.mateus.projetoRestPuc.entities

import com.fasterxml.jackson.annotation.*
import io.swagger.annotations.ApiModelProperty
import java.sql.Date
import javax.persistence.*

@Entity
@Table(name = "Jogadores", catalog = "")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class PlayerEntity (
    @get:Id
    @get:GeneratedValue(strategy= GenerationType.IDENTITY)
    @get:Column(name = "id", nullable = false, insertable = false, updatable = false)
    var id: Int? = null,
    @get:Basic
    @get:Column(name = "nome", nullable = false)
    var name: String? = null,
    @get:Basic
    @get:Column(name = "pais", nullable = false)
    var country: String? = null,
    @get:Basic
    @get:Column(name = "dataNascimento", nullable = false)
    @JsonFormat(pattern="dd/MM/yyyy")
    @ApiModelProperty(dataType = "java.sql.Date")
    var birthDate: Date? = null,
    @get:Basic
    @get:Column(name = "codigoInscricao", nullable = false)
    var codePlayer: String? = null,

    @get:ManyToOne(fetch = FetchType.LAZY)
    @get:JoinColumn(name = "timeId", referencedColumnName = "id")
    var playerTeam: TeamEntity? = null,
    @get:OneToMany(mappedBy = "player", cascade = [CascadeType.REMOVE])
    @JsonIgnore
    var transfers: List<TransferEntity>? = null

)