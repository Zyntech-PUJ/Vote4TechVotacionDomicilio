package com.vote4tech.domicilio2.data.remote.api

import com.vote4tech.domicilio2.data.remote.dto.CandidatoRemotoDto
import com.vote4tech.domicilio2.data.remote.dto.CiudadanoRemotoDto
import com.vote4tech.domicilio2.data.remote.dto.EleccionRemotaDto
import com.vote4tech.domicilio2.data.remote.dto.FuncionarioRemotoDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CentralApi {
    @GET("eleccion/activas")
    suspend fun getEleccionesActivas(): List<EleccionRemotaDto>

    @GET("eleccion/{idEleccion}/candidatos")
    suspend fun getCandidatos(@Path("idEleccion") idEleccion: Long): List<CandidatoRemotoDto>

    @GET("ciudadano/domicilio")
    suspend fun getCiudadanosDomicilio(): List<CiudadanoRemotoDto>

    @GET("ciudadano/todos")
    suspend fun getCiudadanosTodos(): List<CiudadanoRemotoDto>

    @GET("funcionario/activos")
    suspend fun getFuncionarios(): List<FuncionarioRemotoDto>

    @GET("config/ping")
    suspend fun ping(): Response<Any>
}
