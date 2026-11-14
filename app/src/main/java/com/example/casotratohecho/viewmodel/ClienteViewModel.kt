package com.example.casotratohecho.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.casotratohecho.data.Cliente
import com.example.casotratohecho.data.ClienteDao
import com.example.casotratohecho.data.remote.RetrofitClient
import com.example.casotratohecho.data.repository.ClienteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ClienteViewModel(
    private val repository: ClienteRepository
) : ViewModel() {

    val clientes: StateFlow<List<Cliente>> = repository.clientes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        sincronizarClientes()
    }

    fun sincronizarClientes() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.sincronizarDesdeBackend()
            } catch (e: Exception) {
                _errorMessage.value = "Sin conexión con el servidor. Mostrando datos locales."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun agregarCliente(cliente: Cliente) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.crearCliente(cliente)
            } catch (e: Exception) {
                _errorMessage.value = "Error al guardar cliente: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarCliente(cliente: Cliente) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.actualizarCliente(cliente)
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar cliente: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarCliente(cliente: Cliente) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.eliminarCliente(cliente)
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar cliente: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun limpiarError() {
        _errorMessage.value = null
    }
}

class ClienteViewModelFactory(
    private val clienteDao: ClienteDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClienteViewModel::class.java)) {
            val repository = ClienteRepository(
                clienteDao = clienteDao,
                backendApi = RetrofitClient.backendApi
            )
            @Suppress("UNCHECKED_CAST")
            return ClienteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}