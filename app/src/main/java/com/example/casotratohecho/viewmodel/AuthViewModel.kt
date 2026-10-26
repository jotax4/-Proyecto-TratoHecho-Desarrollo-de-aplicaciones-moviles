package com.example.casotratohecho.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.casotratohecho.data.Usuario
import com.example.casotratohecho.data.UsuarioDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val usuarioDao: UsuarioDao) : ViewModel() {

    private val _usuarioLogueado = MutableStateFlow<Usuario?>(null)
    val usuarioLogueado: StateFlow<Usuario?> = _usuarioLogueado

    fun registrar(
        nombre: String,
        correo: String,
        contrasena: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val existente = usuarioDao.obtenerPorCorreo(correo)
            if (existente != null) {
                onError("El correo ya se encuentra registrado.")
            } else {
                val nuevoUsuario = Usuario(
                    nombre = nombre,
                    correo = correo,
                    contrasena = contrasena
                )
                usuarioDao.registrarUsuario(nuevoUsuario)
                onSuccess()
            }
        }
    }

    fun login(
        correo: String,
        contrasena: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val usuario = usuarioDao.autenticar(correo, contrasena)
            if (usuario != null) {
                _usuarioLogueado.value = usuario
                onSuccess()
            } else {
                onError("Credenciales incorrectas. Verifique su correo o contraseña.")
            }
        }
    }
}

class AuthViewModelFactory(private val usuarioDao: UsuarioDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(usuarioDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}