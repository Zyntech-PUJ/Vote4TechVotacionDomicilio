package com.vote4tech.domicilio2.data.remote.api

import com.vote4tech.domicilio2.data.remote.dto.CandidatoRemotoDto
import com.vote4tech.domicilio2.data.remote.dto.CiudadanoRemotoDto
import com.vote4tech.domicilio2.data.remote.dto.EleccionRemotaDto
import retrofit2.http.GET
import retrofit2.http.Path

interface CentralApi {
    @GET("eleccion/activas")
    suspend fun getEleccionesActivas(): List<EleccionRemotaDto>

    @GET("eleccion/{idEleccion}/candidatos")
    suspend fun getCandidatos(@Path("idEleccion") idEleccion: Long): List<CandidatoRemotoDto>

    @GET("ciudadano/domicilio")
    suspend fun getCiudadanosDomicilio(): List<CiudadanoRemotoDto>
}
