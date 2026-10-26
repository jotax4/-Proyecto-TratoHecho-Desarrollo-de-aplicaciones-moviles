package com.example.casotratohecho.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.casotratohecho.viewmodel.AuthViewModel
import com.example.casotratohecho.viewmodel.ClienteViewModel
import com.example.casotratohecho.viewmodel.CotizacionViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Registro : Screen("registro")
    object Clientes : Screen("clientes")
    object FormularioCliente : Screen("formulario_cliente")
    object DetalleCliente : Screen("detalle_cliente/{clienteId}") {
        fun createRoute(clienteId: Int) = "detalle_cliente/$clienteId"
    }
    object FormularioCotizacion : Screen("formulario_cotizacion/{clienteId}") {
        fun createRoute(clienteId: Int) = "formulario_cotizacion/$clienteId"
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    clienteViewModel: ClienteViewModel,
    cotizacionViewModel: CotizacionViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginExitoso = {
                    navController.navigate(Screen.Clientes.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onIrARegistro = { navController.navigate(Screen.Registro.route) }
            )
        }

        composable(Screen.Registro.route) {
            RegistroScreen(
                authViewModel = authViewModel,
                onRegistroExitoso = {
                    navController.navigate(Screen.Clientes.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onVolverClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Clientes.route) {
            ClienteScreen(
                viewModel = clienteViewModel,
                onClienteClick = { clienteId ->
                    navController.navigate(Screen.DetalleCliente.createRoute(clienteId))
                },
                onAgregarClienteClick = {
                    navController.navigate(Screen.FormularioCliente.route)
                }
            )
        }

        composable(Screen.FormularioCliente.route) {
            FormularioClienteScreen(
                viewModel = clienteViewModel,
                onVolverClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.DetalleCliente.route,
            arguments = listOf(navArgument("clienteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getInt("clienteId") ?: return@composable
            DetalleClienteScreen(
                clienteId = clienteId,
                clienteViewModel = clienteViewModel,
                cotizacionViewModel = cotizacionViewModel,
                onVolverClick = { navController.popBackStack() },
                onAgregarCotizacionClick = {
                    navController.navigate(Screen.FormularioCotizacion.createRoute(clienteId))
                }
            )
        }

        composable(
            route = Screen.FormularioCotizacion.route,
            arguments = listOf(navArgument("clienteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getInt("clienteId") ?: return@composable
            FormularioCotizacionScreen(
                clienteId = clienteId,
                viewModel = cotizacionViewModel,
                onVolverClick = { navController.popBackStack() }
            )
        }
    }
}