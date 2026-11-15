package com.example.casotratohecho.viewmodel

import com.example.casotratohecho.data.remote.IndicadoresResponse
import com.example.casotratohecho.data.remote.MindicadorApiService
import com.example.casotratohecho.data.remote.MindicadorClient
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class IndicadoresViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var api: MindicadorApiService

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        api = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init carga indicadores exitosamente y actualiza UF y Dolar en uiState`() = runTest {
        val respuestaMock = mockk<IndicadoresResponse>(relaxed = true)
        every { respuestaMock.uf?.valor } returns 38150.75
        every { respuestaMock.dolar?.valor } returns 962.40
        coEvery { api.obtenerIndicadores() } returns respuestaMock

        val viewModel = IndicadoresViewModel(api)
        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertFalse(state.error)
        assertEquals(38150.75, state.ufValor!!, 0.01)
        assertEquals(962.40, state.dolarValor!!, 0.01)
        coVerify(exactly = 1) { api.obtenerIndicadores() }
    }

    @Test
    fun `cargarIndicadores maneja respuesta con indicadores nulos sin lanzar error`() = runTest {
        val respuestaVacia = mockk<IndicadoresResponse>(relaxed = true)
        every { respuestaVacia.uf } returns null
        every { respuestaVacia.dolar } returns null
        coEvery { api.obtenerIndicadores() } returns respuestaVacia

        val viewModel = IndicadoresViewModel(api)
        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertFalse(state.error)
        assertNull(state.ufValor)
        assertNull(state.dolarValor)
    }

    @Test
    fun `cargarIndicadores activa estado de error cuando falla la llamada a la API`() = runTest {
        coEvery { api.obtenerIndicadores() } throws RuntimeException("Sin conexión a mindicador.cl")

        val viewModel = IndicadoresViewModel(api)
        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertTrue(state.error)
        assertNull(state.ufValor)
        assertNull(state.dolarValor)
    }

    @Test
    fun `reintentar cargarIndicadores limpia error previo y recupera valores`() = runTest {
        val respuestaOk = mockk<IndicadoresResponse>(relaxed = true)
        every { respuestaOk.uf?.valor } returns 38200.0
        every { respuestaOk.dolar?.valor } returns 955.0

        coEvery { api.obtenerIndicadores() } throws RuntimeException("Timeout") andThen respuestaOk

        val viewModel = IndicadoresViewModel(api)
        assertTrue(viewModel.uiState.value.error)

        viewModel.cargarIndicadores()

        assertFalse(viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(38200.0, viewModel.uiState.value.ufValor!!, 0.01)
        assertEquals(955.0, viewModel.uiState.value.dolarValor!!, 0.01)
    }

    @Test
    fun `constructor por defecto utiliza MindicadorClient correctamente`() = runTest {
        mockkObject(MindicadorClient)
        try {
            val respuestaMock = mockk<IndicadoresResponse>(relaxed = true)
            every { respuestaMock.uf?.valor } returns 38000.0
            every { respuestaMock.dolar?.valor } returns 950.0
            coEvery { api.obtenerIndicadores() } returns respuestaMock
            every { MindicadorClient.api } returns api

            val viewModel = IndicadoresViewModel()

            assertFalse(viewModel.uiState.value.isLoading)
            assertFalse(viewModel.uiState.value.error)
        } finally {
            unmockkObject(MindicadorClient)
        }
    }
}