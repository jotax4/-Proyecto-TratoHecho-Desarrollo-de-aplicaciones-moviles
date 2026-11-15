package com.example.casotratohecho.data.repository

import com.example.casotratohecho.data.Cotizacion
import com.example.casotratohecho.data.CotizacionDao
import com.example.casotratohecho.data.remote.TratoHechoApiService
import kotlinx.coroutines.flow.Flow

class CotizacionRepository(
    private val cotizacionDao: CotizacionDao,
    private val backendApi: TratoHechoApiService
) {
    fun observarCotizacionesPorCliente(clienteId: Int): Flow<List<Cotizacion>> {
        return cotizacionDao.getCotizacionesPorCliente(clienteId)
    }

    // GET desde Spring Boot y actualización en Room (IE 3.1.3)
    suspend fun sincronizarDesdeBackend(clienteId: Int) {
        val remotas = backendApi.obtenerCotizacionesPorCliente(clienteId)
        remotas.forEach { cotizacion ->
            cotizacionDao.insertarCotizacion(cotizacion.copy(sincronizado = true))
        }
        cotizacionDao.marcarTodasComoSincronizadas()
    }

    // POST hacia Spring Boot + respaldo local
    suspend fun crearCotizacion(cotizacion: Cotizacion) {
        val creada = backendApi.crearCotizacion(cotizacion)
        cotizacionDao.insertarCotizacion(creada.copy(sincronizado = true))
    }

    // PUT hacia Spring Boot + actualización local
    suspend fun actualizarCotizacion(cotizacion: Cotizacion) {
        val actualizada = backendApi.actualizarCotizacion(cotizacion.id, cotizacion)
        cotizacionDao.insertarCotizacion(actualizada.copy(sincronizado = true))
    }

    // DELETE hacia Spring Boot + eliminación local
    suspend fun eliminarCotizacion(cotizacion: Cotizacion) {
        if (cotizacion.id > 0) {
            backendApi.eliminarCotizacion(cotizacion.id)
        }
        cotizacionDao.eliminarCotizacion(cotizacion)
    }
}