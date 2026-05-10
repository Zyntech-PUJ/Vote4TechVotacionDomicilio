package com.vote4tech.domicilio2.data.remote.dto

import com.google.gson.annotations.SerializedName

data class EleccionRemotaDto(
    @SerializedName("idEleccion") val id: Long,
    val nombre: String,
    val descripcion: String?,
    val estado: String
)

data class CandidatoRemotoDto(
    @SerializedName("idCandidato") val id: Long,
    val nombre: String,
    @SerializedName("nombrePartido") val partido: String,
    val numero: String
)

data class CiudadanoRemotoDto(
    val cedula: String,
    val nombre: String,
    val habilitadoDomicilio: Boolean
)

data class VotoCouchDto(
    val _id: String,
    val cedula: String,
    val idEleccion: Long,
    val idCandidato: Long,
    val idFuncionario: String,
    val timestamp: Long
)
