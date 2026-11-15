package com.example.casotratohecho.data.repository

import com.example.casotratohecho.data.Cliente
import com.example.casotratohecho.data.ClienteDao
import com.example.casotratohecho.data.remote.TratoHechoApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ClienteRepositoryTest {

    private lateinit var clienteDao: ClienteDao
    private lateinit var backendApi: TratoHechoApiService
    private lateinit var repository: ClienteRepository

    @Before
    fun setUp() {
        clienteDao = mockk(relaxed = true)
        backendApi = mockk(relaxed = true)
    }

    @Test
    fun `clientes expone el flujo obtenido desde clienteDao`() = runTest {
        val listaMock = listOf(mockk<Cliente>(relaxed = true))
        every { clienteDao.obtenerTodos() } returns flowOf(listaMock)

        repository = ClienteRepository(clienteDao, backendApi)
        val resultado = repository.clientes.first()

        assertEquals(1, resultado.size)
    }

    @Test
    fun `sincronizarDesdeBackend reemplaza clientes locales con los remotos sincronizados`() = runTest {
        val remoto1 = mockk<Cliente>(relaxed = true)
        val remoto1Sync = mockk<Cliente>(relaxed = true)
        val remoto2 = mockk<Cliente>(relaxed = true)
        val remoto2Sync = mockk<Cliente>(relaxed = true)

        every { remoto1.copy(sincronizado = true) } returns remoto1Sync
        every { remoto2.copy(sincronizado = true) } returns remoto2Sync
        coEvery { backendApi.obtenerClientes() } returns listOf(remoto1, remoto2)

        repository = ClienteRepository(clienteDao, backendApi)
        repository.sincronizarDesdeBackend()

        coVerifyOrder {
            backendApi.obtenerClientes()
            clienteDao.eliminarTodos()
            clienteDao.insertarTodos(listOf(remoto1Sync, remoto2Sync))
        }
    }

    @Test
    fun `crearCliente envia POST al backend y guarda copia sincronizada en room`() = runTest {
        val nuevo = mockk<Cliente>(relaxed = true)
        val creado = mockk<Cliente>(relaxed = true)
        val creadoSync = mockk<Cliente>(relaxed = true)

        every { creado.copy(sincronizado = true) } returns creadoSync
        coEvery { backendApi.crearCliente(nuevo) } returns creado

        repository = ClienteRepository(clienteDao, backendApi)
        repository.crearCliente(nuevo)

        coVerify(exactly = 1) { backendApi.crearCliente(nuevo) }
        coVerify(exactly = 1) { clienteDao.insertar(creadoSync) }
    }

    @Test
    fun `actualizarCliente envia PUT al backend y actualiza registro sincronizado`() = runTest {
        val editado = mockk<Cliente>(relaxed = true)
        val actualizado = mockk<Cliente>(relaxed = true)
        val actualizadoSync = mockk<Cliente>(relaxed = true)

        every { editado.id } returns 4
        every { actualizado.copy(sincronizado = true) } returns actualizadoSync
        coEvery { backendApi.actualizarCliente(4, editado) } returns actualizado

        repository = ClienteRepository(clienteDao, backendApi)
        repository.actualizarCliente(editado)

        coVerify(exactly = 1) { backendApi.actualizarCliente(4, editado) }
        coVerify(exactly = 1) { clienteDao.actualizar(actualizadoSync) }
    }

    @Test
    fun `eliminarCliente con id positivo elimina en backend y en room`() = runTest {
        val cliente = mockk<Cliente>(relaxed = true)
        every { cliente.id } returns 9

        repository = ClienteRepository(clienteDao, backendApi)
        repository.eliminarCliente(cliente)

        coVerify(exactly = 1) { backendApi.eliminarCliente(9) }
        coVerify(exactly = 1) { clienteDao.eliminar(cliente) }
    }

    @Test
    fun `eliminarCliente con id cero solo elimina localmente sin llamar al backend`() = runTest {
        val clienteLocal = mockk<Cliente>(relaxed = true)
        every { clienteLocal.id } returns 0

        repository = ClienteRepository(clienteDao, backendApi)
        repository.eliminarCliente(clienteLocal)

        coVerify(exactly = 0) { backendApi.eliminarCliente(any()) }
        coVerify(exactly = 1) { clienteDao.eliminar(clienteLocal) }
    }
}