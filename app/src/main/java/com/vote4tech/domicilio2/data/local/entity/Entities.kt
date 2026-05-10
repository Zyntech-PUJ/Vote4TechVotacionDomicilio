package com.vote4tech.domicilio2.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "elecciones")
data class EleccionLocalEntity(
    @PrimaryKey val id: Long,
    val nombre: String,
    val descripcion: String?,
    val estado: String
)

@Entity(tableName = "candidatos")
data class CandidatoLocalEntity(
    @PrimaryKey val id: Long,
    val nombre: String,
    val partido: String,
    val numero: Int,
    val idEleccion: Long
)

@Entity(tableName = "ciudadanos")
data class CiudadanoLocalEntity(
    @PrimaryKey val cedula: String,
    val nombre: String,
    val habilitadoDomicilio: Boolean
)

enum class EstadoVoto { PENDIENTE, SINCRONIZADO }

@Entity(
    tableName = "votos_locales",
    indices = [Index(value = ["cedula", "idEleccion"], unique = true)]
)
data class VotoLocalEntity(
    @PrimaryKey val id: String,  // UUID generado localmente
    val cedula: String,
    val idEleccion: Long,
    val idCandidato: Long,
    val idFuncionario: String,
    val timestamp: Long,
    val estado: EstadoVoto = EstadoVoto.PENDIENTE
)
