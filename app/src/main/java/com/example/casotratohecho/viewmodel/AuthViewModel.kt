package com.example.casotratohecho.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.casotratohecho.data.Usuario
import com.example.casotratohecho.data.UsuarioDao
import com.example.casotratohecho.data.remote.RetrofitClient
import com.example.casotratohecho.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _usuarioLogueado = MutableStateFlow<Usuario?>(null)
    val usuarioLogueado: StateFlow<Usuario?> = _usuarioLogueado.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun registrar(
        nombre: String,
        correo: String,
        contrasena: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (nombre.isBlank() || correo.isBlank() || contrasena.isBlank()) {
            onError("Todos los campos son obligatorios.")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val resultado = repository.registrar(nombre, correo, contrasena)
            _isLoading.value = false

            resultado.fold(
                onSuccess = { onSuccess() },
                onFailure = { error -> onError(error.message ?: "Error al registrar usuario.") }
            )
        }
    }

    fun login(
        correo: String,
        contrasena: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (correo.isBlank() || contrasena.isBlank()) {
            onError("Ingrese su correo y contraseña.")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val resultado = repository.login(correo, contrasena)
            _isLoading.value = false

            resultado.fold(
                onSuccess = { usuario ->
                    _usuarioLogueado.value = usuario
                    onSuccess()
                },
                onFailure = { error ->
                    onError(error.message ?: "Credenciales incorrectas.")
                }
            )
        }
    }

    fun logout() {
        _usuarioLogueado.value = null
    }
}

class AuthViewModelFactory(
    private val usuarioDao: UsuarioDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            val repository = AuthRepository(
                usuarioDao = usuarioDao,
                backendApi = RetrofitClient.backendApi
            )
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}