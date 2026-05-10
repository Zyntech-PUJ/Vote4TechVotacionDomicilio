package com.vote4tech.domicilio2.data.remote.api

import com.vote4tech.domicilio2.data.remote.dto.VotoCouchDto
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

interface CouchDbApi {
    @PUT("{id}")
    suspend fun guardarVoto(
        @Path("id") id: String,
        @Body voto: VotoCouchDto
    ): VotoCouchDto
}
