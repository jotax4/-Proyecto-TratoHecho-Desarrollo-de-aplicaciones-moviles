package com.example.casotratohecho.data.remote

import com.example.casotratohecho.data.Cliente
import com.example.casotratohecho.data.Cotizacion
import com.example.casotratohecho.data.Usuario
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class LoginRequest(
    @SerializedName("correo") val correo: String,
    @SerializedName("contrasena") val contrasena: String
)

interface TratoHechoApiService {
    // Autenticación
    @POST("api/auth/registro")
    suspend fun registrarUsuario(@Body usuario: Usuario): Usuario

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Usuario

    // CRUD Clientes
    @GET("api/clientes")
    suspend fun obtenerClientes(): List<Cliente>

    @POST("api/clientes")
    suspend fun crearCliente(@Body cliente: Cliente): Cliente

    @PUT("api/clientes/{id}")
    suspend fun actualizarCliente(@Path("id") id: Int, @Body cliente: Cliente): Cliente

    @DELETE("api/clientes/{id}")
    suspend fun eliminarCliente(@Path("id") id: Int)

    // CRUD Cotizaciones
    @GET("api/cotizaciones/cliente/{clienteId}")
    suspend fun obtenerCotizacionesPorCliente(@Path("clienteId") clienteId: Int): List<Cotizacion>

    @POST("api/cotizaciones")
    suspend fun crearCotizacion(@Body cotizacion: Cotizacion): Cotizacion

    @PUT("api/cotizaciones/{id}")
    suspend fun actualizarCotizacion(@Path("id") id: Int, @Body cotizacion: Cotizacion): Cotizacion

    @DELETE("api/cotizaciones/{id}")
    suspend fun eliminarCotizacion(@Path("id") id: Int)
}