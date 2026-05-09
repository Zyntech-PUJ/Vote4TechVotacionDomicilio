package com.votacion.domicilio.data.remote

import com.votacion.domicilio.data.remote.api.CentralApi
import com.votacion.domicilio.data.remote.api.CouchDbApi
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClients {

    // ── Cliente para el servidor central ──────────────────────
    private var centralRetrofit: Retrofit? = null

    fun initCentral(baseUrl: String) {
        centralRetrofit = buildRetrofit(baseUrl, null)
    }

    val centralApi: CentralApi
        get() = (centralRetrofit ?: error("CentralApi no inicializado. Llama initCentral() primero."))
            .create(CentralApi::class.java)

    // ── Cliente para CouchDB central ──────────────────────────
    private var couchRetrofit: Retrofit? = null

    fun initCouchDb(baseUrl: String, user: String, password: String) {
        val credentials = Credentials.basic(user, password)
        couchRetrofit = buildRetrofit(
            url = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/",
            authHeader = credentials
        )
    }

    val couchDbApi: CouchDbApi
        get() = (couchRetrofit ?: error("CouchDbApi no inicializado. Llama initCouchDb() primero."))
            .create(CouchDbApi::class.java)

    // ── Builder compartido ────────────────────────────────────
    private fun buildRetrofit(url: String, authHeader: String?): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val clientBuilder = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

        if (authHeader != null) {
            clientBuilder.addInterceptor { chain ->
                val req = chain.request().newBuilder()
                    .header("Authorization", authHeader)
                    .build()
                chain.proceed(req)
            }
        }

        return Retrofit.Builder()
            .baseUrl(if (url.endsWith("/")) url else "$url/")
            .client(clientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
