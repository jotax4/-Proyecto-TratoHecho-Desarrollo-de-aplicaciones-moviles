package com.example.casotratohecho.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.casotratohecho.data.Cliente
import com.example.casotratohecho.data.ClienteDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ClienteViewModel(private val clienteDao: ClienteDao) : ViewModel() {

    val clientes: StateFlow<List<Cliente>> = clienteDao.getTodosLosClientes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun agregarCliente(cliente: Cliente) {
        viewModelScope.launch {
            clienteDao.insertarCliente(cliente)
        }
    }

    fun eliminarCliente(cliente: Cliente) {
        viewModelScope.launch {
            clienteDao.eliminarCliente(cliente)
        }
    }

    fun sincronizarDatos() {
        viewModelScope.launch {
            clienteDao.marcarTodosComoSincronizados()
        }
    }
}

class ClienteViewModelFactory(private val clienteDao: ClienteDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClienteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClienteViewModel(clienteDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}