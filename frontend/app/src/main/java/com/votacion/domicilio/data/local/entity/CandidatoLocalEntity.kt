package com.votacion.domicilio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Caché local de candidatos descargados del servidor central.
 * Incluye datos del partido embebidos para no necesitar join.
 */
@Entity(tableName = "candidato_local")
data class CandidatoLocalEntity(
    @PrimaryKey val idCandidato: Long,
    val nombre: String,
    val numero: String,
    val fotoUrl: String?,
    val idEleccion: Long,
    val idLista: Long,
    val nombrePartido: String?,
    val siglaPartido: String?,
    val logoPartido: String?,
    val actualizadoEn: Long = System.currentTimeMillis()
)
