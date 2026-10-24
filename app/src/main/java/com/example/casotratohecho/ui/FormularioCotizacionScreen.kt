package com.example.casotratohecho.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.casotratohecho.data.Cotizacion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioCotizacionScreen(
    clienteId: Int,
    onVolver: () -> Unit,
    onGuardarCotizacion: (Cotizacion) -> Unit
) {
    var descripcion by remember { mutableStateOf("") }
    var montoText by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("Pendiente") }
    var errorMonto by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Cotización / Trato") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val montoDouble = montoText.toDoubleOrNull()
                    if (montoDouble == null || montoDouble <= 0) {
                        errorMonto = true
                    } else {
                        val nuevaCotizacion = Cotizacion(
                            clienteId = clienteId,
                            descripcion = descripcion.trim(),
                            monto = montoDouble,
                            estado = estado,
                            sincronizado = false
                        )
                        onGuardarCotizacion(nuevaCotizacion)
                        onVolver()
                    }
                }
            ) {
                Icon(Icons.Default.Check, contentDescription = "Guardar Cotización")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = montoText,
                onValueChange = {
                    montoText = it
                    if (errorMonto) errorMonto = false
                },
                label = { Text("Monto *") },
                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = errorMonto,
                supportingText = {
                    if (errorMonto) Text("Ingrese un monto válido", color = MaterialTheme.colorScheme.error)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción del Trato / Servicio") },
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4
            )
        }
    }
}