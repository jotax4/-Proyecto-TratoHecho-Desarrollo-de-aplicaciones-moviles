package com.example.casotratohecho.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CotizacionDao {
    @Query("SELECT * FROM cotizaciones WHERE clienteId = :clienteId ORDER BY id DESC")
    fun getCotizacionesPorCliente(clienteId: Int): Flow<List<Cotizacion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCotizacion(cotizacion: Cotizacion)

    @Update
    suspend fun actualizarCotizacion(cotizacion: Cotizacion)

    @Delete
    suspend fun eliminarCotizacion(cotizacion: Cotizacion)

    @Query("UPDATE cotizaciones SET sincronizado = 1 WHERE sincronizado = 0")
    suspend fun marcarTodasComoSincronizadas()
}