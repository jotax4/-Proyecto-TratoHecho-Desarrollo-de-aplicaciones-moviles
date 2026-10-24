package com.example.casotratohecho.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.casotratohecho.data.Cotizacion
import com.example.casotratohecho.data.CotizacionDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CotizacionViewModel(private val cotizacionDao: CotizacionDao) : ViewModel() {

    fun getCotizacionesPorCliente(clienteId: Int): StateFlow<List<Cotizacion>> {
        return cotizacionDao.getCotizacionesPorCliente(clienteId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun agregarCotizacion(cotizacion: Cotizacion) {
        viewModelScope.launch {
            cotizacionDao.insertarCotizacion(cotizacion)
        }
    }

    fun eliminarCotizacion(cotizacion: Cotizacion) {
        viewModelScope.launch {
            cotizacionDao.eliminarCotizacion(cotizacion)
        }
    }

    fun sincronizarCotizaciones() {
        viewModelScope.launch {
            cotizacionDao.marcarTodasComoSincronizadas()
        }
    }
}

class CotizacionViewModelFactory(private val cotizacionDao: CotizacionDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CotizacionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CotizacionViewModel(cotizacionDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}