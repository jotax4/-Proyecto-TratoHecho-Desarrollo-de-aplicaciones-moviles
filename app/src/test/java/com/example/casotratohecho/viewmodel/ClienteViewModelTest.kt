package com.example.casotratohecho.viewmodel

import com.example.casotratohecho.data.Cliente
import com.example.casotratohecho.data.repository.ClienteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ClienteViewModelTest {

    // Regla para que las corrutinas (viewModelScope.launch) se ejecuten correctamente en pruebas
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: ClienteRepository
    private lateinit var viewModel: ClienteViewModel

    @Before
    fun setUp() {
        // 1. Creamos el repositorio falso
        repository = mockk()

        // 2. Configuramos el flujo de clientes que el ViewModel pide al inicializarse
        every { repository.clientes } returns flowOf(emptyList())

        // 3. Simulamos un éxito por defecto para la sincronización inicial del bloque init{}
        coEvery { repository.sincronizarDesdeBackend() } returns Unit

        // 4. Instanciamos el ViewModel
        viewModel = ClienteViewModel(repository)
    }

    @Test
    fun `agregarCliente exitoso oculta el loading y no deja error`() = runTest {
        val cliente = Cliente(1, "Juan", "Perez", "12345678-9", "juan@test.com", "123456789")
        coEvery { repository.crearCliente(cliente) } returns Unit

        viewModel.agregarCliente(cliente)

        coVerify { repository.crearCliente(cliente) }
        assertEquals(false, viewModel.isLoading.value)
        assertNull(viewModel.errorMessage.value)
    }

    @Test
    fun `agregarCliente con falla actualiza el mensaje de error`() = runTest {
        val cliente = Cliente(1, "Juan", "Perez", "12345678-9", "juan@test.com", "123456789")
        val errorMsg = "Error de red"
        coEvery { repository.crearCliente(cliente) } throws Exception(errorMsg)

        viewModel.agregarCliente(cliente)

        assertEquals("Error al guardar cliente: $errorMsg", viewModel.errorMessage.value)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun `actualizarCliente exitoso oculta el loading y no deja error`() = runTest {
        val cliente = Cliente(1, "Juan", "Perez", "12345678-9", "juan@test.com", "123456789")
        coEvery { repository.actualizarCliente(cliente) } returns Unit

        viewModel.actualizarCliente(cliente)

        coVerify { repository.actualizarCliente(cliente) }
        assertNull(viewModel.errorMessage.value)
    }

    @Test
    fun `eliminarCliente con falla actualiza el mensaje de error`() = runTest {
        val cliente = Cliente(1, "Juan", "Perez", "12345678-9", "juan@test.com", "123456789")
        val errorMsg = "Base de datos caída"
        coEvery { repository.eliminarCliente(cliente) } throws Exception(errorMsg)

        viewModel.eliminarCliente(cliente)

        assertEquals("Error al eliminar cliente: $errorMsg", viewModel.errorMessage.value)
    }

    @Test
    fun `sincronizarClientes con error muestra mensaje de fallback local`() = runTest {
        coEvery { repository.sincronizarDesdeBackend() } throws Exception("Timeout")

        viewModel.sincronizarClientes()

        assertEquals("Sin conexión con el servidor. Mostrando datos locales.", viewModel.errorMessage.value)
    }

    @Test
    fun `limpiarError establece errorMessage en null`() = runTest {
        // Forzamos un error primero
        coEvery { repository.sincronizarDesdeBackend() } throws Exception("Error inicial")
        viewModel.sincronizarClientes()

        // Verificamos que el error existe
        assertEquals("Sin conexión con el servidor. Mostrando datos locales.", viewModel.errorMessage.value)

        // Limpiamos y verificamos
        viewModel.limpiarError()
        assertNull(viewModel.errorMessage.value)
    }
}