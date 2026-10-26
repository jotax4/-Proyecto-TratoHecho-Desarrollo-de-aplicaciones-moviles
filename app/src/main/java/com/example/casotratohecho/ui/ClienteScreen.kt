package com.example.casotratohecho.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.casotratohecho.data.Cliente
import com.example.casotratohecho.viewmodel.ClienteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteScreen(
    viewModel: ClienteViewModel,
    onClienteClick: (Int) -> Unit,
    onAgregarClienteClick: () -> Unit
) {
    val clientes by viewModel.clientes.collectAsStateWithLifecycle()
    var clienteAEliminar by remember { mutableStateOf<Cliente?>(null) }

    // Diálogo de confirmación para eliminar
    clienteAEliminar?.let { cliente ->
        AlertDialog(
            onDismissRequest = { clienteAEliminar = null },
            title = { Text("Eliminar Cliente") },
            text = { Text("¿Estás seguro de que deseas eliminar a ${cliente.nombre}? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarCliente(cliente)
                        clienteAEliminar = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { clienteAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clientes - Trato Hecho") },
                actions = {
                    IconButton(onClick = { /* Acción opcional de sincronización */ }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Sincronizar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAgregarClienteClick) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Cliente")
            }
        }
    ) { innerPadding ->
        if (clientes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay clientes registrados.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(clientes, key = { it.id }) { cliente ->
                    ClienteCard(
                        cliente = cliente,
                        onClick = { onClienteClick(cliente.id) },
                        onEliminarClick = { clienteAEliminar = cliente }
                    )
                }
            }
        }
    }
}

@Composable
fun ClienteCard(
    cliente: Cliente,
    onClick: () -> Unit,
    onEliminarClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cliente.nombre,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = cliente.empresa,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Tel: ${cliente.telefono}",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (cliente.sincronizado) "⏳ Sincronizado" else "⏳ Pendiente de sincronización",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (cliente.sincronizado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }

            IconButton(onClick = onEliminarClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar Cliente",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}