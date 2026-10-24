package com.example.casotratohecho

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.casotratohecho.data.AppDatabase
import com.example.casotratohecho.ui.AppNavigation
import com.example.casotratohecho.ui.theme.CasoTratoHechoTheme
import com.example.casotratohecho.viewmodel.ClienteViewModel
import com.example.casotratohecho.viewmodel.ClienteViewModelFactory
import com.example.casotratohecho.viewmodel.CotizacionViewModel
import com.example.casotratohecho.viewmodel.CotizacionViewModelFactory

class MainActivity : ComponentActivity() {

    private val database by lazy { AppDatabase.getDatabase(context = this) }

    private val clienteViewModel: ClienteViewModel by viewModels {
        ClienteViewModelFactory(clienteDao = database.clienteDao())
    }

    private val cotizacionViewModel: CotizacionViewModel by viewModels {
        CotizacionViewModelFactory(cotizacionDao = database.cotizacionDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CasoTratoHechoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavigation(
                        navController = navController,
                        clienteViewModel = clienteViewModel,
                        cotizacionViewModel = cotizacionViewModel
                    )
                }
            }
        }
    }
}