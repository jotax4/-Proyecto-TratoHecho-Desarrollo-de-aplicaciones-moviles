package com.example.casotratohecho.data.repository

import com.example.casotratohecho.data.Cotizacion
import com.example.casotratohecho.data.CotizacionDao
import com.example.casotratohecho.data.remote.TratoHechoApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CotizacionRepositoryTest {

    private lateinit var cotizacionDao: CotizacionDao
    private lateinit var backendApi: TratoHechoApiService
    private lateinit var repository: CotizacionRepository

    @Before
    fun setUp() {
        cotizacionDao = mockk(relaxed = true)
        backendApi = mockk(relaxed = true)
        repository = CotizacionRepository(cotizacionDao, backendApi)
    }

    @Test
    fun `observarCotizacionesPorCliente retorna flujo desde dao`() = runTest {
        val listaMock = listOf(mockk<Cotizacion>(relaxed = true))
        every { cotizacionDao.getCotizacionesPorCliente(10) } returns flowOf(listaMock)

        val resultado = repository.observarCotizacionesPorCliente(10).first()

        assertEquals(1, resultado.size)
        verify(exactly = 1) { cotizacionDao.getCotizacionesPorCliente(10) }
    }

    @Test
    fun `sincronizarDesdeBackend guarda cotizaciones remotas sincronizadas en room`() = runTest {
        val remota1 = mockk<Cotizacion>(relaxed = true)
        val remota1Sync = mockk<Cotizacion>(relaxed = true)
        val remota2 = mockk<Cotizacion>(relaxed = true)
        val remota2Sync = mockk<Cotizacion>(relaxed = true)

        every { remota1.copy(sincronizado = true) } returns remota1Sync
        every { remota2.copy(sincronizado = true) } returns remota2Sync
        coEvery { backendApi.obtenerCotizacionesPorCliente(3) } returns listOf(remota1, remota2)

        repository.sincronizarDesdeBackend(3)

        coVerify(exactly = 1) { cotizacionDao.insertarCotizacion(remota1Sync) }
        coVerify(exactly = 1) { cotizacionDao.insertarCotizacion(remota2Sync) }
        coVerify(exactly = 1) { cotizacionDao.marcarTodasComoSincronizadas() }
    }

    @Test
    fun `crearCotizacion envia POST al backend y guarda copia sincronizada`() = runTest {
        val nueva = mockk<Cotizacion>(relaxed = true)
        val creada = mockk<Cotizacion>(relaxed = true)
        val creadaSync = mockk<Cotizacion>(relaxed = true)

        every { creada.copy(sincronizado = true) } returns creadaSync
        coEvery { backendApi.crearCotizacion(nueva) } returns creada

        repository.crearCotizacion(nueva)

        coVerify(exactly = 1) { backendApi.crearCotizacion(nueva) }
        coVerify(exactly = 1) { cotizacionDao.insertarCotizacion(creadaSync) }
    }

    @Test
    fun `actualizarCotizacion envia PUT al backend y guarda copia sincronizada`() = runTest {
        val editada = mockk<Cotizacion>(relaxed = true)
        val actualizada = mockk<Cotizacion>(relaxed = true)
        val actualizadaSync = mockk<Cotizacion>(relaxed = true)

        every { editada.id } returns 7
        every { actualizada.copy(sincronizado = true) } returns actualizadaSync
        coEvery { backendApi.actualizarCotizacion(7, editada) } returns actualizada

        repository.actualizarCotizacion(editada)

        coVerify(exactly = 1) { backendApi.actualizarCotizacion(7, editada) }
        coVerify(exactly = 1) { cotizacionDao.insertarCotizacion(actualizadaSync) }
    }

    @Test
    fun `eliminarCotizacion con id positivo elimina en backend y en room`() = runTest {
        val cotizacion = mockk<Cotizacion>(relaxed = true)
        every { cotizacion.id } returns 12

        repository.eliminarCotizacion(cotizacion)

        coVerify(exactly = 1) { backendApi.eliminarCotizacion(12) }
        coVerify(exactly = 1) { cotizacionDao.eliminarCotizacion(cotizacion) }
    }

    @Test
    fun `eliminarCotizacion con id cero solo elimina localmente sin llamar al backend`() = runTest {
        val cotizacionLocal = mockk<Cotizacion>(relaxed = true)
        every { cotizacionLocal.id } returns 0

        repository.eliminarCotizacion(cotizacionLocal)

        coVerify(exactly = 0) { backendApi.eliminarCotizacion(any()) }
        coVerify(exactly = 1) { cotizacionDao.eliminarCotizacion(cotizacionLocal) }
    }
}