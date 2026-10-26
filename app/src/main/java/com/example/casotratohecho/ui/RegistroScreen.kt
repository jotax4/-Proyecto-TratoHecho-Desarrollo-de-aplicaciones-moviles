package com.example.casotratohecho.ui

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.casotratohecho.viewmodel.AuthViewModel

private fun vibrar(context: Context) {
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

@Composable
fun RegistroScreen(
    authViewModel: AuthViewModel,
    onRegistroExitoso: () -> Unit,
    onVolverClick: () -> Unit
) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf<String?>(null) }

    fun validarYRegistrar() {
        when {
            nombre.isBlank() -> errorMensaje = "El nombre es obligatorio."
            correo.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> {
                errorMensaje = "Ingrese un correo electrónico válido."
            }
            contrasena.length < 6 -> {
                errorMensaje = "La contraseña debe tener al menos 6 caracteres."
            }
            contrasena != confirmarContrasena -> {
                errorMensaje = "Las contraseñas no coinciden."
            }
            else -> {
                authViewModel.registrar(
                    nombre = nombre.trim(),
                    correo = correo.trim(),
                    contrasena = contrasena,
                    onSuccess = {
                        vibrar(context)
                        onRegistroExitoso()
                    },
                    onError = { mensaje -> errorMensaje = mensaje }
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Crear Cuenta", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        AnimatedVisibility(visible = errorMensaje != null) {
            errorMensaje?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
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

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it; errorMensaje = null },
            label = { Text("Correo Electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it; errorMensaje = null },
            label = { Text("Contraseña (mínimo 6 caracteres)") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmarContrasena,
            onValueChange = { confirmarContrasena = it; errorMensaje = null },
            label = { Text("Confirmar Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { validarYRegistrar() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onVolverClick) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}