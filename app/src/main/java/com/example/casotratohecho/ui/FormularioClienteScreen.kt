package com.example.casotratohecho.ui

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.casotratohecho.data.Cliente
import com.example.casotratohecho.viewmodel.ClienteViewModel

// Función auxiliar para activar la respuesta hápica (Vibración)
private fun vibrarDispositivo(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        @Suppress("DEPRECATION")
        vibrator.vibrate(100)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioClienteScreen(
    viewModel: ClienteViewModel,
    onVolverClick: () -> Unit
) {
    val context = LocalContext.current

    var nombre by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var empresa by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }

    var errorMensaje by remember { mutableStateOf<String?>(null) }

    val rutRegex = Regex("^[0-9]{1,2}\\.?[0-9]{3}\\.?[0-9]{3}-[0-9kK]{1}$")

    fun validarYGuardar() {
        val rutLimpio = rut.trim().replace(" ", "")
        val telefonoLimpio = telefono.trim().filter { it.isDigit() }

        when {
            nombre.isBlank() -> errorMensaje = "El nombre es obligatorio."
            rutLimpio.isBlank() -> errorMensaje = "El RUT es obligatorio."
            !rutRegex.matches(rutLimpio) -> errorMensaje = "Formato de RUT inválido (Ej: 12.345.678-K)."
            empresa.isBlank() -> errorMensaje = "La empresa es obligatoria."
            telefonoLimpio.length < 8 -> errorMensaje = "Ingrese un número de teléfono válido (mínimo 8 dígitos)."
            correo.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> {
                errorMensaje = "Ingrese un correo electrónico válido."
            }
            else -> {
                val telefonoFinal = if (telefonoLimpio.length == 9 && telefonoLimpio.startsWith("9")) {
                    "+56 $telefonoLimpio"
                } else {
                    "+56 9 $telefonoLimpio"
                }

                vibrarDispositivo(context) // Ejecuta el recurso nativo de vibración
                viewModel.agregarCliente(
                    Cliente(
                        nombre = nombre.trim(),
                        rut = rutLimpio.uppercase(),
                        empresa = empresa.trim(),
                        telefono = telefonoFinal,
                        correo = correo.trim(),
                        sincronizado = false
                    )
                )
                onVolverClick()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Cliente") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { validarYGuardar() }) {
                        Icon(Icons.Default.Check, contentDescription = "Guardar Cliente")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Animación fluida de aparición/desaparición del error
            AnimatedVisibility(
                visible = errorMensaje != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                errorMensaje?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it; errorMensaje = null },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = rut,
                onValueChange = { input ->
                    val filtrado = input.filter { it.isDigit() || it == '.' || it == '-' || it == 'k' || it == 'K' }
                    rut = filtrado
                    errorMensaje = null
                },
                label = { Text("RUT (Ej: 12.345.678-K)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = empresa,
                onValueChange = { empresa = it; errorMensaje = null },
                label = { Text("Empresa") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = telefono,
                onValueChange = { input ->
                    telefono = input.filter { it.isDigit() }
                    errorMensaje = null
                },
                label = { Text("Teléfono (9 dígitos)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it; errorMensaje = null },
                label = { Text("Correo Electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = { validarYGuardar() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Cliente")
            }
        }
    }
}