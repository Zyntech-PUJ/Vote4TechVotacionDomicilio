package com.vote4tech.domicilio2.data.remote

import android.util.Base64
import com.vote4tech.domicilio2.data.remote.api.CentralApi
import com.vote4tech.domicilio2.data.remote.api.CouchDbApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClients(
    centralBaseUrl: String,
    couchDbBaseUrl: String,
    couchUser: String,
    couchPassword: String
) {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val baseOkHttp = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

    private val couchOkHttp = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .addInterceptor(Interceptor { chain ->
            val credentials = Base64.encodeToString(
                "$couchUser:$couchPassword".toByteArray(),
                Base64.NO_WRAP
            )
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Basic $credentials")
                .build()
            chain.proceed(request)
        })
        .build()

    val centralApi: CentralApi = Retrofit.Builder()
        .baseUrl(centralBaseUrl.trimEnd('/') + "/")
        .client(baseOkHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CentralApi::class.java)

    // CouchDB URL ya incluye el nombre de la base: http://host:5984/votos_domicilio
    val couchDbApi: CouchDbApi = Retrofit.Builder()
        .baseUrl(couchDbBaseUrl.trimEnd('/') + "/")
        .client(couchOkHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CouchDbApi::class.java)
}
