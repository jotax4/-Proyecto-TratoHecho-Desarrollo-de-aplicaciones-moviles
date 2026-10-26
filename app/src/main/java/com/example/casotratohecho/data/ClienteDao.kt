package com.example.casotratohecho.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {
    @Query("SELECT * FROM clientes")
    fun obtenerTodos(): Flow<List<Cliente>>

    @Insert
    suspend fun insertar(cliente: Cliente)

    @Delete
    suspend fun eliminar(cliente: Cliente)
}