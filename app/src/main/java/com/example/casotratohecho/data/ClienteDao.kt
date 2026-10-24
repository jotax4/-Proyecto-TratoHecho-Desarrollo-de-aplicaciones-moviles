package com.example.casotratohecho.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {
    @Query("SELECT * FROM clientes ORDER BY id DESC")
    fun getTodosLosClientes(): Flow<List<Cliente>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCliente(cliente: Cliente)

    @Delete
    suspend fun eliminarCliente(cliente: Cliente)

    @Query("UPDATE clientes SET sincronizado = 1 WHERE sincronizado = 0")
    suspend fun marcarTodosComoSincronizados()
}