package com.example.casotratohecho.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.casotratohecho.data.Cotizacion
import com.example.casotratohecho.viewmodel.CotizacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioCotizacionScreen(
    clienteId: Int,
    viewModel: CotizacionViewModel,
    onVolverClick: () -> Unit
) {
    var descripcion by remember { mutableStateOf("") }
    var montoText by remember { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf<String?>(null) }

    fun validarYGuardar() {
        val montoParsed = montoText.toDoubleOrNull()
        when {
            descripcion.isBlank() -> errorMensaje = "La descripción no puede estar vacía."
            montoParsed == null || montoParsed <= 0 -> errorMensaje = "Ingrese un monto mayor a 0."
            else -> {
                viewModel.agregarCotizacion(
                    Cotizacion(
                        clienteId = clienteId,
                        descripcion = descripcion.trim(),
                        monto = montoParsed,
                        estado = "Pendiente",
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
                title = { Text("Nueva Cotización") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { validarYGuardar() }) {
                        Icon(Icons.Default.Check, contentDescription = "Guardar Cotización")
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
            errorMensaje?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it; errorMensaje = null },
                label = { Text("Descripción del proyecto / servicio") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            OutlinedTextField(
                value = montoText,
                onValueChange = { montoText = it; errorMensaje = null },
                label = { Text("Monto ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = { validarYGuardar() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Cotización")
            }
        }
    }
}