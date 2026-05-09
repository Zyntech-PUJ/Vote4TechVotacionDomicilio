package com.votacion.domicilio.data.remote.api

import com.votacion.domicilio.data.remote.dto.CandidatoRemotoDto
import com.votacion.domicilio.data.remote.dto.CiudadanoRemotoDto
import com.votacion.domicilio.data.remote.dto.EleccionRemotaDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Interfaz Retrofit para consultar el servidor central (Vote4TechVotacionBack
 * o Vote4TechRegistraduriaBack) y descargar datos electorales.
 */
interface CentralApi {

    /** Descarga todas las elecciones activas con sus candidatos. */
    @GET("eleccion/activas")
    suspend fun getEleccionesActivas(): Response<List<EleccionRemotaDto>>

    /** Descarga candidatos de una elección específica. */
    @GET("eleccion/{id}/candidatos")
    suspend fun getCandidatos(@Path("id") idEleccion: Long): Response<List<CandidatoRemotoDto>>

    /**
     * Descarga el padrón de ciudadanos habilitados para voto domiciliario.
     * Endpoint de la registraduría.
     */
    @GET("ciudadano/domicilio")
    suspend fun getCiudadanosDomicilio(): Response<List<CiudadanoRemotoDto>>
}
