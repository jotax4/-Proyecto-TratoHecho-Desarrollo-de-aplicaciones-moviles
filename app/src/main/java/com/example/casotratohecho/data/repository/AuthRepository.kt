package com.example.casotratohecho.data.repository

import com.example.casotratohecho.data.Usuario
import com.example.casotratohecho.data.UsuarioDao
import com.example.casotratohecho.data.remote.LoginRequest
import com.example.casotratohecho.data.remote.TratoHechoApiService
import retrofit2.HttpException
import java.io.IOException

class AuthRepository(
    private val usuarioDao: UsuarioDao,
    private val backendApi: TratoHechoApiService
) {
    suspend fun registrar(nombre: String, correo: String, contrasena: String): Result<Usuario> {
        return try {
            val nuevoUsuario = Usuario(
                nombre = nombre.trim(),
                correo = correo.trim(),
                contrasena = contrasena
            )
            val usuarioCreado = backendApi.registrarUsuario(nuevoUsuario)
            usuarioDao.registrarUsuario(usuarioCreado.copy(contrasena = ""))
            Result.success(usuarioCreado)
        } catch (e: HttpException) {
            val mensaje = if (e.code() == 409 || e.code() == 400) {
                "El correo ya se encuentra registrado."
            } else {
                "Error del servidor (${e.code()}) al registrar usuario."
            }
            Result.failure(Exception(mensaje))
        } catch (e: IOException) {
            e.printStackTrace()
            Result.failure(Exception("No se pudo conectar con el servidor. Verifique su red."))
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Error local (${e.javaClass.simpleName}): ${e.localizedMessage}"))
        }
    }

    suspend fun login(correo: String, contrasena: String): Result<Usuario> {
        return try {
            val credenciales = LoginRequest(
                correo = correo.trim(),
                contrasena = contrasena
            )
            val usuarioAutenticado = backendApi.login(credenciales)
            usuarioDao.registrarUsuario(usuarioAutenticado.copy(contrasena = ""))
            Result.success(usuarioAutenticado)
        } catch (e: HttpException) {
            val mensaje = if (e.code() == 401 || e.code() == 404) {
                "Credenciales incorrectas. Verifique su correo o contraseña."
            } else {
                "Error del servidor (${e.code()}) al iniciar sesión."
            }
            Result.failure(Exception(mensaje))
        } catch (e: IOException) {
            e.printStackTrace()
            Result.failure(Exception("Sin conexión con el servidor. Intente nuevamente."))
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Error local (${e.javaClass.simpleName}): ${e.localizedMessage}"))
        }
    }
}