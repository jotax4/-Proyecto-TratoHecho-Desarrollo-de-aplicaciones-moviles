package com.example.casotratohecho.ui

class ClienteScreeen {
    package com.example.casotratohecho.ui

    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.lazy.items
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.Add
    import androidx.compose.material3.*
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.collectAsState
    import androidx.compose.runtime.getValue
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.unit.dp
    import com.example.casotratohecho.data.Cliente
    import com.example.casotratohecho.viewmodel.ClienteViewModel

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ClienteScreen(viewModel: ClienteViewModel) {
        // Observa el flujo de datos de Room en tiempo real
        val clientes by viewModel.clientes.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Clientes - Trato Hecho") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    // Inserta un cliente de prueba al hacer clic
                    viewModel.agregarCliente(
                        Cliente(
                            rut = "11.111.111-1",
                            nombre = "Juan Pérez",
                            empresa = "Constructora JP",
                            correo = "juan@jp.cl",
                            telefono = "+56912345678"
                        )
                    )
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar Cliente")
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(clientes) { cliente ->
                    ClienteCard(cliente = cliente)
                }
            }
        }
    }

    @Composable
    fun ClienteCard(cliente: Cliente) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = cliente.nombre, style = MaterialTheme.typography.titleMedium)
                Text(text = cliente.empresa, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = if (cliente.sincronizado) "Sincronizado" else "Pendiente de sincronización",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (cliente.sincronizado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}