package com.example.casotratohecho.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "clientes")
data class Cliente(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("rut")
    val rut: String,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("empresa")
    val empresa: String,

    @SerializedName("correo")
    val correo: String,

    @SerializedName("telefono")
    val telefono: String,

    val sincronizado: Boolean = false
)