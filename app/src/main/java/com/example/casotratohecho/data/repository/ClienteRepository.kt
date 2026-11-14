package com.example.casotratohecho.data.repository

import com.example.casotratohecho.data.Cliente
import com.example.casotratohecho.data.ClienteDao
import com.example.casotratohecho.data.remote.TratoHechoApiService
import kotlinx.coroutines.flow.Flow

class ClienteRepository(
    private val clienteDao: ClienteDao,
    private val backendApi: TratoHechoApiService
) {
    val clientes: Flow<List<Cliente>> = clienteDao.obtenerTodos()

    suspend fun sincronizarDesdeBackend() {
        val remotos = backendApi.obtenerClientes()
        val sincronizados = remotos.map { it.copy(sincronizado = true) }
        clienteDao.eliminarTodos()
        clienteDao.insertarTodos(sincronizados)
    }

    suspend fun crearCliente(cliente: Cliente) {
        val creado = backendApi.crearCliente(cliente)
        clienteDao.insertar(creado.copy(sincronizado = true))
    }

    suspend fun actualizarCliente(cliente: Cliente) {
        val actualizado = backendApi.actualizarCliente(cliente.id, cliente)
        clienteDao.actualizar(actualizado.copy(sincronizado = true))
    }

    suspend fun eliminarCliente(cliente: Cliente) {
        if (cliente.id > 0) {
            backendApi.eliminarCliente(cliente.id)
        }
        clienteDao.eliminar(cliente)
    }
}