package com.votacion.domicilio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Caché local de elecciones descargadas del servidor central. */
@Entity(tableName = "eleccion_local")
data class EleccionLocalEntity(
    @PrimaryKey val idEleccion: Long,
    val nombre: String,
    val tipo: String,
    val estado: String,
    val listaAbierta: Boolean,
    val actualizadoEn: Long = System.currentTimeMillis()
)
