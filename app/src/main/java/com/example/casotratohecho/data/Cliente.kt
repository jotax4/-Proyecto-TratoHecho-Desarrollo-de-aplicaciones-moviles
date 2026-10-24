package com.example.casotratohecho.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clientes")
data class Cliente(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val rut: String,
    val nombre: String,
    val empresa: String,
    val correo: String,
    val telefono: String,
    val sincronizado: Boolean = false
)