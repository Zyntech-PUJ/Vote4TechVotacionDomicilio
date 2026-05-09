package com.votacion.domicilio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Borrador de voto en progreso — persiste en Room para fault tolerance. */
@Entity(tableName = "voto_draft")
data class VotoDraftEntity(
    @PrimaryKey val id: String,
    val cedula: String,
    val nombreCiudadano: String,
    val idEleccion: Long,
    val nombreEleccion: String,
    val tipoSeleccion: String? = null,
    val idSeleccion: Long? = null,
    val nombreSeleccion: String? = null,
    val estado: String = EstadoDraft.IDENTIFICADO.name,
    val creadoEn: Long = System.currentTimeMillis()
)

enum class EstadoDraft {
    IDENTIFICADO,
    ELECCION_SELECCIONADA,
    CANDIDATO_SELECCIONADO,
    CONFIRMADO,
    GUARDADO   // Guardado en Room como VotoLocal. Aún no sync'd a CouchDB.
}
