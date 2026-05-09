package com.votacion.domicilio.data.remote.dto

// ── Pull desde servidor central ────────────────────────────────
data class EleccionRemotaDto(
    val idEleccion: Long,
    val nombre: String,
    val tipo: String,
    val estado: String,
    val listaAbierta: Boolean,
    val candidatos: List<CandidatoRemotoDto>?
)

data class CandidatoRemotoDto(
    val idCandidato: Long,
    val nombre: String,
    val numero: String,
    val fotoUrl: String?,
    val idLista: Long,
    val nombrePartido: String?,
    val siglaPartido: String?,
    val logoPartido: String?
)

data class CiudadanoRemotoDto(
    val cedula: String,
    val nombre: String,
    val genero: String?
)

// ── Push a CouchDB central ─────────────────────────────────────
data class VotoCouchDto(
    val _id: String,
    val idEleccion: Long,
    val tipoMesa: String = "DOMICILIO",
    val tipoSeleccion: String,
    val idSeleccion: Long,
    val timestamp: String,
    val fuente: String = "DOMICILIO"
)
