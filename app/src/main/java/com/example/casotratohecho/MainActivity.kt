package com.example.casotratohecho

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.casotratohecho.data.AppDatabase
import com.example.casotratohecho.ui.AppNavigation
import com.example.casotratohecho.ui.theme.CasoTratoHechoTheme
import com.example.casotratohecho.viewmodel.ClienteViewModel
import com.example.casotratohecho.viewmodel.ClienteViewModelFactory
import com.example.casotratohecho.viewmodel.CotizacionViewModel
import com.example.casotratohecho.viewmodel.CotizacionViewModelFactory

class MainActivity : ComponentActivity() {

    private val database by lazy { AppDatabase.getDatabase(this) }

    private val clienteViewModel: ClienteViewModel by viewModels {
        ClienteViewModelFactory(database.clienteDao())
    }

    private val cotizacionViewModel: CotizacionViewModel by viewModels {
        CotizacionViewModelFactory(database.cotizacionDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CasoTratoHechoTheme {
                AppNavigation(
                    clienteViewModel = clienteViewModel,
                    cotizacionViewModel = cotizacionViewModel
                )
            }
        }
    }
}