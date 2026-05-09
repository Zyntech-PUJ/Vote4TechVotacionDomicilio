package com.votacion.domicilio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Caché local de ciudadanos habilitados para voto domiciliario.
 * Se descarga del servidor central antes del día de elecciones.
 */
@Entity(tableName = "ciudadano_local")
data class CiudadanoLocalEntity(
    @PrimaryKey val cedula: String,
    val nombre: String,
    val genero: String?,
    val actualizadoEn: Long = System.currentTimeMillis()
)
