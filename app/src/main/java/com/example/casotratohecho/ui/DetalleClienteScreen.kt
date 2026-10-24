package com.example.casotratohecho.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.casotratohecho.data.Cliente
import com.example.casotratohecho.data.Cotizacion
import com.example.casotratohecho.viewmodel.CotizacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleClienteScreen(
    cliente: Cliente?,
    cotizacionViewModel: CotizacionViewModel,
    onVolver: () -> Unit,
    onNuevaCotizacionClick: () -> Unit
) {
    val cotizaciones by cotizacionViewModel.getCotizacionesPorCliente(cliente?.id ?: 0)
        .collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(cliente?.nombre ?: "Detalle del Cliente") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNuevaCotizacionClick) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Cotización")
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
            cliente?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Empresa: ${it.empresa}", style = MaterialTheme.typography.bodyLarge)
                        Text(text = "RUT: ${it.rut}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Correo: ${it.correo}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Teléfono: ${it.telefono}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Text(
                text = "Historial de Tratos y Cotizaciones",
                style = MaterialTheme.typography.titleMedium
            )

            if (cotizaciones.isEmpty()) {
                Text(
                    text = "No hay cotizaciones registradas para este cliente.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cotizaciones) { cotizacion ->
                        CotizacionCard(cotizacion = cotizacion)
                    }
                }
            }
        }
    }
}

@Composable
fun CotizacionCard(cotizacion: Cotizacion) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Monto: $${cotizacion.monto}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = cotizacion.estado,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = cotizacion.descripcion,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}