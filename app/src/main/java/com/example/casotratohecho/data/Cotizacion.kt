package com.example.casotratohecho.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cotizaciones")
data class Cotizacion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val clienteId: Int,
    val descripcion: String,
    val monto: Double,
    val estado: String = "Pendiente",
    val sincronizado: Boolean = false
)