package com.example.casotratohecho.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
    navController: NavHostController,
    clienteViewModel: ClienteViewModel,
    cotizacionViewModel: CotizacionViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.ListaClientes.route
    ) {
        // 1. Lista de Clientes
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
                viewModel = clienteViewModel,
                onVolverClick = {
                    navController.popBackStack()
                }
            )
        }

        // 3. Detalle de Cliente y sus Cotizaciones
        composable(
            route = Screen.DetalleCliente.route,
            arguments = listOf(navArgument("clienteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getInt("clienteId") ?: 0
            DetalleClienteScreen(
                clienteId = clienteId,
                clienteViewModel = clienteViewModel,
                cotizacionViewModel = cotizacionViewModel,
                onVolverClick = { navController.popBackStack() },
                onAgregarCotizacionClick = {
                    navController.navigate(Screen.FormularioCotizacion.passClienteId(clienteId))
                }
            )
        }

        // 4. Formulario Crear Cotización
        composable(
            route = Screen.FormularioCotizacion.route,
            arguments = listOf(navArgument("clienteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getInt("clienteId") ?: 0
            FormularioCotizacionScreen(
                clienteId = clienteId,
                viewModel = cotizacionViewModel,
                onVolverClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}