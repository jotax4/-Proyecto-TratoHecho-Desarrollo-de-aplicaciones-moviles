package com.example.casotratohecho.viewmodel

import androidx.lifecycle.ViewModel
import com.example.casotratohecho.data.Usuario
import com.example.casotratohecho.data.UsuarioDao
import com.example.casotratohecho.data.remote.RetrofitClient
import com.example.casotratohecho.data.remote.TratoHechoApiService
import com.example.casotratohecho.data.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: AuthRepository
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        viewModel = AuthViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- TESTS DE REGISTRO ---

    @Test
    fun `registrar con campos vacios invoca onError sin llamar al repositorio`() = runTest {
        var mensajeError: String? = null
        var exitoLlamado = false

        viewModel.registrar("", "juan@test.com", "123", { exitoLlamado = true }, { mensajeError = it })
        assertEquals("Todos los campos son obligatorios.", mensajeError)

        viewModel.registrar("Juan", "   ", "123", { exitoLlamado = true }, { mensajeError = it })
        assertEquals("Todos los campos son obligatorios.", mensajeError)

        viewModel.registrar("Juan", "juan@test.com", "", { exitoLlamado = true }, { mensajeError = it })
        assertEquals("Todos los campos son obligatorios.", mensajeError)

        assertFalse(exitoLlamado)
        coVerify(exactly = 0) { repository.registrar(any(), any(), any()) }
    }

    @Test
    fun `registrar exitoso invoca onSuccess y desactiva isLoading`() = runTest {
        val usuario = Usuario(id = 1, nombre = "Juan", correo = "juan@test.com", contrasena = "")
        coEvery { repository.registrar("Juan", "juan@test.com", "123") } returns Result.success(usuario)

        var exitoLlamado = false
        var errorLlamado = false

        viewModel.registrar(
            nombre = "Juan",
            correo = "juan@test.com",
            contrasena = "123",
            onSuccess = { exitoLlamado = true },
            onError = { errorLlamado = true }
        )

        assertTrue(exitoLlamado)
        assertFalse(errorLlamado)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `registrar fallido invoca onError con mensaje o texto por defecto`() = runTest {
        coEvery { repository.registrar("Juan", "juan@test.com", "123") } returns
                Result.failure(Exception("El correo ya se encuentra registrado."))

        var mensajeError: String? = null
        viewModel.registrar("Juan", "juan@test.com", "123", {}, { mensajeError = it })
        assertEquals("El correo ya se encuentra registrado.", mensajeError)

        coEvery { repository.registrar("Juan", "juan@test.com", "123") } returns
                Result.failure(Exception())

        viewModel.registrar("Juan", "juan@test.com", "123", {}, { mensajeError = it })
        assertEquals("Error al registrar usuario.", mensajeError)
        assertFalse(viewModel.isLoading.value)
    }

    // --- TESTS DE LOGIN Y LOGOUT ---

    @Test
    fun `login con campos vacios invoca onError sin llamar al repositorio`() = runTest {
        var mensajeError: String? = null
        var exitoLlamado = false

        viewModel.login("", "123", { exitoLlamado = true }, { mensajeError = it })
        assertEquals("Ingrese su correo y contraseña.", mensajeError)

        viewModel.login("juan@test.com", "  ", { exitoLlamado = true }, { mensajeError = it })
        assertEquals("Ingrese su correo y contraseña.", mensajeError)

        assertFalse(exitoLlamado)
        coVerify(exactly = 0) { repository.login(any(), any()) }
    }

    @Test
    fun `login exitoso guarda usuarioLogueado e invoca onSuccess`() = runTest {
        val usuario = Usuario(id = 2, nombre = "Juan", correo = "juan@test.com", contrasena = "")
        coEvery { repository.login("juan@test.com", "123") } returns Result.success(usuario)

        var exitoLlamado = false
        viewModel.login(
            correo = "juan@test.com",
            contrasena = "123",
            onSuccess = { exitoLlamado = true },
            onError = {}
        )

        assertTrue(exitoLlamado)
        assertEquals(usuario, viewModel.usuarioLogueado.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `login fallido invoca onError y mantiene usuarioLogueado en null`() = runTest {
        coEvery { repository.login("juan@test.com", "mal") } returns
                Result.failure(Exception("Credenciales incorrectas."))

        var mensajeError: String? = null
        viewModel.login("juan@test.com", "mal", {}, { mensajeError = it })
        assertEquals("Credenciales incorrectas.", mensajeError)
        assertNull(viewModel.usuarioLogueado.value)

        coEvery { repository.login("juan@test.com", "mal") } returns
                Result.failure(Exception())

        viewModel.login("juan@test.com", "mal", {}, { mensajeError = it })
        assertEquals("Credenciales incorrectas.", mensajeError)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `logout limpia el estado de usuarioLogueado`() = runTest {
        val usuario = Usuario(id = 2, nombre = "Juan", correo = "juan@test.com", contrasena = "")
        coEvery { repository.login("juan@test.com", "123") } returns Result.success(usuario)

        viewModel.login("juan@test.com", "123", {}, {})
        assertNotNull(viewModel.usuarioLogueado.value)

        viewModel.logout()
        assertNull(viewModel.usuarioLogueado.value)
    }

    // --- TEST DE FACTORY ---

    @Test
    fun `factory crea AuthViewModel y lanza excepcion con clase desconocida`() {
        mockkObject(RetrofitClient)
        try {
            val apiMock = mockk<TratoHechoApiService>(relaxed = true)
            every { RetrofitClient.backendApi } returns apiMock

            val dao = mockk<UsuarioDao>(relaxed = true)
            val factory = AuthViewModelFactory(dao)

            val vm = factory.create(AuthViewModel::class.java)
            assertNotNull(vm)

            class DummyViewModel : ViewModel()
            try {
                factory.create(DummyViewModel::class.java)
                fail("Debió lanzar IllegalArgumentException")
            } catch (e: IllegalArgumentException) {
                assertEquals("Unknown ViewModel class", e.message)
            }
        } finally {
            unmockkObject(RetrofitClient)
        }
    }
}