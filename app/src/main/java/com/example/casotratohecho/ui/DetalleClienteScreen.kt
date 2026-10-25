package com.example.casotratohecho.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.casotratohecho.data.Cotizacion
import com.example.casotratohecho.viewmodel.ClienteViewModel
import com.example.casotratohecho.viewmodel.CotizacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleClienteScreen(
    clienteId: Int,
    clienteViewModel: ClienteViewModel,
    cotizacionViewModel: CotizacionViewModel,
    onVolverClick: () -> Unit,
    onAgregarCotizacionClick: () -> Unit
) {
    val context = LocalContext.current
    val clientes by clienteViewModel.clientes.collectAsStateWithLifecycle()
    val cliente = clientes.find { it.id == clienteId }

    val cotizacionesState = remember(clienteId) {
        cotizacionViewModel.getCotizacionesPorCliente(clienteId)
    }
    val cotizaciones by cotizacionesState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(cliente?.nombre ?: "Detalle del Cliente") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAgregarCotizacionClick) {
                Icon(Icons.Filled.Add, contentDescription = "Nueva Cotización")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            cliente?.let { cli ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "RUT: ${cli.rut}", style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Empresa: ${cli.empresa}", style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Teléfono: ${cli.telefono}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Correo: ${cli.correo}", style = MaterialTheme.typography.bodyMedium)

                        Spacer(modifier = Modifier.height(12.dp))

                        // Acción nativa: Abre la app de llamadas con el número cargado
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${cli.telefono.replace(" ", "")}")
                                }
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.Phone, contentDescription = "Llamar")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Llamar al Cliente")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Cotizaciones", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (cotizaciones.isEmpty()) {
                Text(
                    text = "No hay cotizaciones registradas para este cliente.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
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
            Text(text = cotizacion.descripcion, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Monto: $${cotizacion.monto}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Estado: ${cotizacion.estado}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}