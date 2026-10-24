package com.example.casotratohecho.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.casotratohecho.data.Cliente
import com.example.casotratohecho.data.Cotizacion
import com.example.casotratohecho.viewmodel.ClienteViewModel
import com.example.casotratohecho.viewmodel.CotizacionViewModel

sealed class Screen(val route: String) {
    object ListaClientes : Screen("lista_clientes")
    object FormularioCliente : Screen("formulario_cliente")
    object DetalleCliente : Screen("detalle_cliente/{clienteId}") {
        fun passClienteId(clienteId: Int) = "detalle_cliente/$clienteId"
    }
    object FormularioCotizacion : Screen("formulario_cotizacion/{clienteId}") {
        fun passClienteId(clienteId: Int) = "formulario_cotizacion/$clienteId"
    }
}

@Composable
fun AppNavigation(
    clienteViewModel: ClienteViewModel,
    cotizacionViewModel: CotizacionViewModel
) {
    val navController = rememberNavController()
    val clientes by clienteViewModel.clientes.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = Screen.ListaClientes.route
    ) {
        // 1. Pantalla Lista de Clientes
        composable(Screen.ListaClientes.route) {
            ClienteScreen(
                viewModel = clienteViewModel,
                onAgregarClienteClick = {
                    navController.navigate(Screen.FormularioCliente.route)
                },
                onClienteClick = { clienteId ->
                    navController.navigate(Screen.DetalleCliente.passClienteId(clienteId))
                }
            )
        }

        // 2. Formulario Crear Cliente
        composable(Screen.FormularioCliente.route) {
            FormularioClienteScreen(
                onVolver = { navController.popBackStack() },
                onGuardarCliente = { cliente: Cliente ->
                    clienteViewModel.agregarCliente(cliente)
                }
            )
        }

        // 3. Detalle de Cliente y sus Cotizaciones
        composable(
            route = Screen.DetalleCliente.route,
            arguments = listOf(navArgument("clienteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getInt("clienteId") ?: return@composable
            val clienteSeleccionado = clientes.find { it.id == clienteId }

            DetalleClienteScreen(
                cliente = clienteSeleccionado,
                cotizacionViewModel = cotizacionViewModel,
                onVolver = { navController.popBackStack() },
                onNuevaCotizacionClick = {
                    navController.navigate(Screen.FormularioCotizacion.passClienteId(clienteId))
                }
            )
        }

        // 4. Formulario Nueva Cotización
        composable(
            route = Screen.FormularioCotizacion.route,
            arguments = listOf(navArgument("clienteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getInt("clienteId") ?: return@composable
            FormularioCotizacionScreen(
                clienteId = clienteId,
                onVolver = { navController.popBackStack() },
                onGuardarCotizacion = { cotizacion: Cotizacion ->
                    cotizacionViewModel.agregarCotizacion(cotizacion)
                }
            )
        }
    }
}