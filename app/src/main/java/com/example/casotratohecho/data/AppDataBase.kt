package com.example.casotratohecho.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Cliente::class, Cotizacion::class],
    version = 2, // Incrementamos la versión para forzar la actualización limpia del esquema
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun clienteDao(): ClienteDao
    abstract fun cotizacionDao(): CotizacionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "trato_hecho_database"
                )
                    .fallbackToDestructiveMigration() // Recrea las tablas si cambió la estructura
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}