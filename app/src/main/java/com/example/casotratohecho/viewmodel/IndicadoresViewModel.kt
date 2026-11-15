package com.example.casotratohecho.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.casotratohecho.data.remote.IndicadoresResponse
import com.example.casotratohecho.data.remote.MindicadorApiService
import com.example.casotratohecho.data.remote.MindicadorClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class IndicadoresUiState(
    val isLoading: Boolean = true,
    val ufValor: Double? = null,
    val dolarValor: Double? = null,
    val error: Boolean = false
)

class IndicadoresViewModel(
    private val api: MindicadorApiService = MindicadorClient.api
) : ViewModel() {

    private val _uiState = MutableStateFlow(IndicadoresUiState())
    val uiState: StateFlow<IndicadoresUiState> = _uiState.asStateFlow()

    init {
        cargarIndicadores()
    }

    fun cargarIndicadores() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = false)
            try {
                val respuesta: IndicadoresResponse = api.obtenerIndicadores()
                _uiState.value = IndicadoresUiState(
                    isLoading = false,
                    ufValor = respuesta.uf?.valor,
                    dolarValor = respuesta.dolar?.valor,
                    error = false
                )
            } catch (e: Exception) {
                _uiState.value = IndicadoresUiState(
                    isLoading = false,
                    error = true
                )
            }
        }
    }
}