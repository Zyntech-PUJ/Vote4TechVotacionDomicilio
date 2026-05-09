package com.votacion.domicilio.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Voto completado guardado localmente.
 * Estado: PENDIENTE → SINCRONIZADO (cuando llega al CouchDB central).
 * Índice único evita que el mismo ciudadano vote dos veces en la misma elección.
 */
@Entity(
    tableName = "voto_local",
    indices = [Index(value = ["cedula", "idEleccion"], unique = true)]
)
data class VotoLocalEntity(
    @PrimaryKey val id: String,
    val cedula: String,
    val idEleccion: Long,
    val tipoSeleccion: String,   // "CANDIDATO" | "LISTA"
    val idSeleccion: Long,
    val estado: String = EstadoVoto.PENDIENTE.name,
    val creadoEn: Long = System.currentTimeMillis(),
    val sincronizadoEn: Long? = null
)

enum class EstadoVoto { PENDIENTE, SINCRONIZADO }
