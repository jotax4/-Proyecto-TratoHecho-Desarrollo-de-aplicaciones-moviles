package com.example.casotratohecho.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "cotizaciones")
data class Cotizacion(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("clienteId")
    val clienteId: Int,

    @SerializedName("descripcion")
    val descripcion: String,

    @SerializedName("monto")
    val monto: Double,

    @SerializedName("estado")
    val estado: String = "Pendiente",

    val sincronizado: Boolean = false
)