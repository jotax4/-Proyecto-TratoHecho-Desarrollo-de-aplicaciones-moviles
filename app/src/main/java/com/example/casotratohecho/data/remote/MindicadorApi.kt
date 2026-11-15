package com.example.casotratohecho.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class IndicadoresResponse(
    val fecha: String = "",
    val uf: IndicadorItem? = null,
    val dolar: IndicadorItem? = null
)

data class IndicadorItem(
    val codigo: String = "",
    val nombre: String = "",
    val valor: Double = 0.0
)

interface MindicadorApiService {
    @GET("api")
    suspend fun obtenerIndicadores(): IndicadoresResponse
}

object MindicadorClient {
    private const val BASE_URL = "https://mindicador.cl/"

    val api: MindicadorApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MindicadorApiService::class.java)
    }
}