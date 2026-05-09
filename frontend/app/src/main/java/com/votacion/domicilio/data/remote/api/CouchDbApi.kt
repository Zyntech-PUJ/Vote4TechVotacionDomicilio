package com.votacion.domicilio.data.remote.api

import com.votacion.domicilio.data.remote.dto.VotoCouchDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Interfaz Retrofit para escribir directamente en CouchDB central.
 * Usa PUT con el UUID del voto como document id.
 * La base URL debe apuntar a: http://couchdb-host:5984/votos_domicilio/
 */
interface CouchDbApi {
    @PUT("{id}")
    suspend fun guardarVoto(
        @Path("id") id: String,
        @Body voto: VotoCouchDto
    ): Response<Unit>
}
