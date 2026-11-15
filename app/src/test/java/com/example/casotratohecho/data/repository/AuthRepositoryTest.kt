package com.example.casotratohecho.data.repository

import com.example.casotratohecho.data.Usuario
import com.example.casotratohecho.data.UsuarioDao
import com.example.casotratohecho.data.remote.LoginRequest
import com.example.casotratohecho.data.remote.TratoHechoApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class AuthRepositoryTest {

    private lateinit var usuarioDao: UsuarioDao
    private lateinit var backendApi: TratoHechoApiService
    private lateinit var repository: AuthRepository

    @Before
    fun setUp() {
        usuarioDao = mockk(relaxed = true)
        backendApi = mockk(relaxed = true)
        repository = AuthRepository(usuarioDao, backendApi)
    }

    private fun crearHttpException(code: Int): HttpException {
        return HttpException(Response.error<Any>(code, "".toResponseBody(null)))
    }

    // --- TESTS DE REGISTRO ---

    @Test
    fun `registrar exitoso envia usuario al backend y guarda copia sin contrasena en room`() = runTest {
        val usuarioEsperado = Usuario(nombre = "Juan", correo = "juan@test.com", contrasena = "123")
        val usuarioCreado = Usuario(id = 1, nombre = "Juan", correo = "juan@test.com", contrasena = "123")

        coEvery { backendApi.registrarUsuario(usuarioEsperado) } returns usuarioCreado

        val resultado = repository.registrar("  Juan  ", "  juan@test.com  ", "123")

        assertTrue(resultado.isSuccess)
        assertEquals(usuarioCreado, resultado.getOrNull())
        coVerify(exactly = 1) { usuarioDao.registrarUsuario(usuarioCreado.copy(contrasena = "")) }
    }

    @Test
    fun `registrar con HttpException 409 o 400 retorna mensaje de correo ya registrado`() = runTest {
        coEvery { backendApi.registrarUsuario(any()) } throws crearHttpException(409)
        val res409 = repository.registrar("Juan", "juan@test.com", "123")
        assertTrue(res409.isFailure)
        assertEquals("El correo ya se encuentra registrado.", res409.exceptionOrNull()?.message)

        coEvery { backendApi.registrarUsuario(any()) } throws crearHttpException(400)
        val res400 = repository.registrar("Juan", "juan@test.com", "123")
        assertTrue(res400.isFailure)
        assertEquals("El correo ya se encuentra registrado.", res400.exceptionOrNull()?.message)
    }

    @Test
    fun `registrar con otro HttpException retorna codigo de error del servidor`() = runTest {
        coEvery { backendApi.registrarUsuario(any()) } throws crearHttpException(500)

        val resultado = repository.registrar("Juan", "juan@test.com", "123")

        assertTrue(resultado.isFailure)
        assertEquals("Error del servidor (500) al registrar usuario.", resultado.exceptionOrNull()?.message)
    }

    @Test
    fun `registrar con IOException retorna error de red`() = runTest {
        coEvery { backendApi.registrarUsuario(any()) } throws IOException("Sin red")

        val resultado = repository.registrar("Juan", "juan@test.com", "123")

        assertTrue(resultado.isFailure)
        assertEquals("No se pudo conectar con el servidor. Verifique su red.", resultado.exceptionOrNull()?.message)
    }

    @Test
    fun `registrar con excepcion generica retorna error local`() = runTest {
        coEvery { backendApi.registrarUsuario(any()) } throws RuntimeException("Fallo inesperado")

        val resultado = repository.registrar("Juan", "juan@test.com", "123")

        assertTrue(resultado.isFailure)
        assertEquals("Error local (RuntimeException): Fallo inesperado", resultado.exceptionOrNull()?.message)
    }

    // --- TESTS DE LOGIN ---

    @Test
    fun `login exitoso autentica en backend y guarda usuario en room sin contrasena`() = runTest {
        val credenciales = LoginRequest(correo = "juan@test.com", contrasena = "123")
        val usuarioAutenticado = Usuario(id = 1, nombre = "Juan", correo = "juan@test.com", contrasena = "123")

        coEvery { backendApi.login(credenciales) } returns usuarioAutenticado

        val resultado = repository.login("  juan@test.com  ", "123")

        assertTrue(resultado.isSuccess)
        assertEquals(usuarioAutenticado, resultado.getOrNull())
        coVerify(exactly = 1) { usuarioDao.registrarUsuario(usuarioAutenticado.copy(contrasena = "")) }
    }

    @Test
    fun `login con HttpException 401 o 404 retorna credenciales incorrectas`() = runTest {
        coEvery { backendApi.login(any()) } throws crearHttpException(401)
        val res401 = repository.login("juan@test.com", "mal")
        assertTrue(res401.isFailure)
        assertEquals("Credenciales incorrectas. Verifique su correo o contraseña.", res401.exceptionOrNull()?.message)

        coEvery { backendApi.login(any()) } throws crearHttpException(404)
        val res404 = repository.login("noexiste@test.com", "123")
        assertTrue(res404.isFailure)
        assertEquals("Credenciales incorrectas. Verifique su correo o contraseña.", res404.exceptionOrNull()?.message)
    }

    @Test
    fun `login con otro HttpException retorna codigo de error del servidor`() = runTest {
        coEvery { backendApi.login(any()) } throws crearHttpException(503)

        val resultado = repository.login("juan@test.com", "123")

        assertTrue(resultado.isFailure)
        assertEquals("Error del servidor (503) al iniciar sesión.", resultado.exceptionOrNull()?.message)
    }

    @Test
    fun `login con IOException retorna error de conexion`() = runTest {
        coEvery { backendApi.login(any()) } throws IOException("Timeout")

        val resultado = repository.login("juan@test.com", "123")

        assertTrue(resultado.isFailure)
        assertEquals("Sin conexión con el servidor. Intente nuevamente.", resultado.exceptionOrNull()?.message)
    }

    @Test
    fun `login con excepcion generica retorna error local`() = runTest {
        coEvery { backendApi.login(any()) } throws IllegalStateException("Estado inválido")

        val resultado = repository.login("juan@test.com", "123")

        assertTrue(resultado.isFailure)
        assertEquals("Error local (IllegalStateException): Estado inválido", resultado.exceptionOrNull()?.message)
    }
}