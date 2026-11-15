package com.example.casotratohecho.viewmodel

import androidx.lifecycle.ViewModel
import com.example.casotratohecho.data.Cotizacion
import com.example.casotratohecho.data.CotizacionDao
import com.example.casotratohecho.data.remote.RetrofitClient
import com.example.casotratohecho.data.remote.TratoHechoApiService
import com.example.casotratohecho.data.repository.CotizacionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CotizacionViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: CotizacionRepository
    private lateinit var viewModel: CotizacionViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        viewModel = CotizacionViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getCotizacionesPorCliente actualiza cotizaciones y sincroniza con backend`() = runTest {
        val cotizacionMock = mockk<Cotizacion>(relaxed = true)
        every { repository.observarCotizacionesPorCliente(1) } returns flowOf(listOf(cotizacionMock))

        val collectJob = backgroundScope.launch(testDispatcher) {
            viewModel.cotizaciones.collect()
        }

        val flujo = viewModel.getCotizacionesPorCliente(1)
        // Segunda llamada con mismo ID cubre la rama sin re-selección
        viewModel.getCotizacionesPorCliente(1)

        assertEquals(1, flujo.value.size)
        coVerify(exactly = 1) { repository.sincronizarDesdeBackend(1) }
        assertFalse(viewModel.isLoading.value)
        collectJob.cancel()
    }

    @Test
    fun `sincronizarCotizaciones con clienteId nulo no llama al repositorio`() = runTest {
        viewModel.sincronizarCotizaciones(null)
        coVerify(exactly = 0) { repository.sincronizarDesdeBackend(any()) }
    }

    @Test
    fun `sincronizarCotizaciones maneja error de red mostrando mensaje local`() = runTest {
        coEvery { repository.sincronizarDesdeBackend(5) } throws RuntimeException("Timeout")

        viewModel.sincronizarCotizaciones(5)

        assertEquals(
            "Sin conexión con el servidor. Mostrando datos locales.",
            viewModel.errorMessage.value
        )
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `agregarCotizacion exitoso y con error`() = runTest {
        val cotizacion = mockk<Cotizacion>(relaxed = true)

        viewModel.agregarCotizacion(cotizacion)
        coVerify(exactly = 1) { repository.crearCotizacion(cotizacion) }
        assertNull(viewModel.errorMessage.value)

        coEvery { repository.crearCotizacion(cotizacion) } throws RuntimeException("Error BD")
        viewModel.agregarCotizacion(cotizacion)
        assertEquals("Error al guardar cotización: Error BD", viewModel.errorMessage.value)

        viewModel.limpiarError()
        assertNull(viewModel.errorMessage.value)
    }

    @Test
    fun `actualizarCotizacion exitoso y con error`() = runTest {
        val cotizacion = mockk<Cotizacion>(relaxed = true)

        viewModel.actualizarCotizacion(cotizacion)
        coVerify(exactly = 1) { repository.actualizarCotizacion(cotizacion) }
        assertNull(viewModel.errorMessage.value)

        coEvery { repository.actualizarCotizacion(cotizacion) } throws RuntimeException("Fallo update")
        viewModel.actualizarCotizacion(cotizacion)
        assertEquals("Error al actualizar cotización: Fallo update", viewModel.errorMessage.value)
    }

    @Test
    fun `eliminarCotizacion exitoso y con error`() = runTest {
        val cotizacion = mockk<Cotizacion>(relaxed = true)

        viewModel.eliminarCotizacion(cotizacion)
        coVerify(exactly = 1) { repository.eliminarCotizacion(cotizacion) }
        assertNull(viewModel.errorMessage.value)

        coEvery { repository.eliminarCotizacion(cotizacion) } throws RuntimeException("Fallo delete")
        viewModel.eliminarCotizacion(cotizacion)
        assertEquals("Error al eliminar cotización: Fallo delete", viewModel.errorMessage.value)
    }

    @Test
    fun `factory crea CotizacionViewModel y lanza excepcion con clase desconocida`() {
        mockkObject(RetrofitClient)
        try {
            val apiMock = mockk<TratoHechoApiService>(relaxed = true)
            every { RetrofitClient.backendApi } returns apiMock

            val dao = mockk<CotizacionDao>(relaxed = true)
            val factory = CotizacionViewModelFactory(dao)

            val vm = factory.create(CotizacionViewModel::class.java)
            assertNotNull(vm)

            class DummyViewModel : ViewModel()
            try {
                factory.create(DummyViewModel::class.java)
                fail("Debió lanzar IllegalArgumentException")
            } catch (e: IllegalArgumentException) {
                assertEquals("Unknown ViewModel class", e.message)
            }
        } finally {
            unmockkObject(RetrofitClient)
        }
    }
}