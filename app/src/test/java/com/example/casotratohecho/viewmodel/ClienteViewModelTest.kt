package com.example.casotratohecho.viewmodel

import androidx.lifecycle.ViewModel
import com.example.casotratohecho.data.Cliente
import com.example.casotratohecho.data.ClienteDao
import com.example.casotratohecho.data.remote.RetrofitClient
import com.example.casotratohecho.data.remote.TratoHechoApiService
import com.example.casotratohecho.data.repository.ClienteRepository
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
class ClienteViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: ClienteRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        every { repository.clientes } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init sincroniza clientes y expone lista desde el repositorio`() = runTest {
        val clienteMock = mockk<Cliente>(relaxed = true)
        every { repository.clientes } returns flowOf(listOf(clienteMock))

        val viewModel = ClienteViewModel(repository)
        val collectJob = backgroundScope.launch(testDispatcher) {
            viewModel.clientes.collect()
        }

        assertEquals(1, viewModel.clientes.value.size)
        coVerify(exactly = 1) { repository.sincronizarDesdeBackend() }
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.errorMessage.value)
        collectJob.cancel()
    }

    @Test
    fun `sincronizarClientes maneja excepcion de red mostrando mensaje local`() = runTest {
        coEvery { repository.sincronizarDesdeBackend() } throws RuntimeException("Timeout")

        val viewModel = ClienteViewModel(repository)

        assertEquals(
            "Sin conexión con el servidor. Mostrando datos locales.",
            viewModel.errorMessage.value
        )
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `agregarCliente exitoso y con error`() = runTest {
        val viewModel = ClienteViewModel(repository)
        val cliente = mockk<Cliente>(relaxed = true)

        viewModel.agregarCliente(cliente)
        coVerify(exactly = 1) { repository.crearCliente(cliente) }
        assertNull(viewModel.errorMessage.value)

        coEvery { repository.crearCliente(cliente) } throws RuntimeException("Error BD")
        viewModel.agregarCliente(cliente)
        assertEquals("Error al guardar cliente: Error BD", viewModel.errorMessage.value)

        viewModel.limpiarError()
        assertNull(viewModel.errorMessage.value)
    }

    @Test
    fun `actualizarCliente exitoso y con error`() = runTest {
        val viewModel = ClienteViewModel(repository)
        val cliente = mockk<Cliente>(relaxed = true)

        viewModel.actualizarCliente(cliente)
        coVerify(exactly = 1) { repository.actualizarCliente(cliente) }
        assertNull(viewModel.errorMessage.value)

        coEvery { repository.actualizarCliente(cliente) } throws RuntimeException("Fallo PUT")
        viewModel.actualizarCliente(cliente)
        assertEquals("Error al actualizar cliente: Fallo PUT", viewModel.errorMessage.value)
    }

    @Test
    fun `eliminarCliente exitoso y con error`() = runTest {
        val viewModel = ClienteViewModel(repository)
        val cliente = mockk<Cliente>(relaxed = true)

        viewModel.eliminarCliente(cliente)
        coVerify(exactly = 1) { repository.eliminarCliente(cliente) }
        assertNull(viewModel.errorMessage.value)

        coEvery { repository.eliminarCliente(cliente) } throws RuntimeException("Fallo DELETE")
        viewModel.eliminarCliente(cliente)
        assertEquals("Error al eliminar cliente: Fallo DELETE", viewModel.errorMessage.value)
    }

    @Test
    fun `factory crea ClienteViewModel y lanza excepcion con clase desconocida`() {
        mockkObject(RetrofitClient)
        try {
            val apiMock = mockk<TratoHechoApiService>(relaxed = true)
            every { RetrofitClient.backendApi } returns apiMock

            val dao = mockk<ClienteDao>(relaxed = true)
            every { dao.obtenerTodos() } returns flowOf(emptyList())

            val factory = ClienteViewModelFactory(dao)
            val vm = factory.create(ClienteViewModel::class.java)
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