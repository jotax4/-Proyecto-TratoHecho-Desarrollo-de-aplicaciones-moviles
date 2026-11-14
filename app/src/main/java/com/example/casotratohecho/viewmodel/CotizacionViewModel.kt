package com.example.casotratohecho.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.casotratohecho.data.Cotizacion
import com.example.casotratohecho.data.CotizacionDao
import com.example.casotratohecho.data.remote.RetrofitClient
import com.example.casotratohecho.data.repository.CotizacionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class CotizacionViewModel(
    private val repository: CotizacionRepository
) : ViewModel() {

    private val _clienteIdActual = MutableStateFlow<Int?>(null)

    val cotizaciones: StateFlow<List<Cotizacion>> = _clienteIdActual
        .flatMapLatest { id ->
            if (id != null) repository.observarCotizacionesPorCliente(id)
            else flowOf(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Estados de la API externa (UF y Dólar - IE 3.1.4)
    private val _valorUf = MutableStateFlow<Double?>(null)
    val valorUf: StateFlow<Double?> = _valorUf.asStateFlow()

    private val _valorDolar = MutableStateFlow<Double?>(null)
    val valorDolar: StateFlow<Double?> = _valorDolar.asStateFlow()

    fun seleccionarCliente(clienteId: Int) {
        _clienteIdActual.value = clienteId
        sincronizarCotizaciones(clienteId)
    }

    // Mantiene compatibilidad con tu llamada actual en UI
    fun getCotizacionesPorCliente(clienteId: Int): StateFlow<List<Cotizacion>> {
        if (_clienteIdActual.value != clienteId) {
            seleccionarCliente(clienteId)
        }
        return cotizaciones
    }

    fun agregarCotizacion(cotizacion: Cotizacion) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.crearCotizacion(cotizacion)
            } catch (e: Exception) {
                _errorMessage.value = "Error al guardar cotización: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarCotizacion(cotizacion: Cotizacion) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.actualizarCotizacion(cotizacion)
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar cotización: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarCotizacion(cotizacion: Cotizacion) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.eliminarCotizacion(cotizacion)
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar cotización: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sincronizarCotizaciones(clienteId: Int? = _clienteIdActual.value) {
        val id = clienteId ?: return
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.sincronizarDesdeBackend(id)
            } catch (e: Exception) {
                _errorMessage.value = "Sin conexión con el servidor. Mostrando datos locales."
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Consulta API externa (mindicador.cl) sin interferir con el microservicio
    fun cargarIndicadoresEconomicos() {
        viewModelScope.launch {
            try {
                val indicadores = repository.obtenerIndicadoresEconomicos()
                _valorUf.value = indicadores.uf.valor
                _valorDolar.value = indicadores.dolar.valor
            } catch (e: Exception) {
                // No bloquea el flujo principal si falla la API externa
                _valorUf.value = null
                _valorDolar.value = null
            }
        }
    }

    fun limpiarError() {
        _errorMessage.value = null
    }
}

class CotizacionViewModelFactory(
    private val cotizacionDao: CotizacionDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CotizacionViewModel::class.java)) {
            val repository = CotizacionRepository(
                cotizacionDao = cotizacionDao,
                backendApi = RetrofitClient.backendApi,
                indicadoresApi = RetrofitClient.indicadoresApi
            )
            @Suppress("UNCHECKED_CAST")
            return CotizacionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}