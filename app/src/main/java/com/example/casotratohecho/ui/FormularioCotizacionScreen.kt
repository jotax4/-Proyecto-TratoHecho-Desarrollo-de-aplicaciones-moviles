package com.example.casotratohecho.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.casotratohecho.data.Cotizacion
import com.example.casotratohecho.ui.theme.StatusPendingBg
import com.example.casotratohecho.ui.theme.StatusPendingText
import com.example.casotratohecho.viewmodel.CotizacionViewModel
import java.text.NumberFormat
import java.util.Locale

private fun formatearVistaPreviaMonto(monto: Double): String {
    val formato = NumberFormat.getNumberInstance(Locale("es", "CL")).apply {
        maximumFractionDigits = if (monto % 1.0 == 0.0) 0 else 2
    }
    return "$${formato.format(monto)}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioCotizacionScreen(
    clienteId: Int,
    viewModel: CotizacionViewModel,
    onVolverClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    var descripcion by remember { mutableStateOf("") }
    var montoText by remember { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf<String?>(null) }

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary

    val montoValido = remember(montoText) {
        montoText.replace(',', '.').toDoubleOrNull()?.takeIf { it > 0 }
    }

    fun validarYGuardar() {
        focusManager.clearFocus()
        val montoParsed = montoText.replace(',', '.').toDoubleOrNull()
        when {
            descripcion.isBlank() -> {
                errorMensaje = "La descripción no puede estar vacía."
            }
            montoParsed == null || montoParsed <= 0 -> {
                errorMensaje = "Ingrese un monto mayor a 0."
            }
            else -> {
                errorMensaje = null
                viewModel.agregarCotizacion(
                    Cotizacion(
                        clienteId = clienteId,
                        descripcion = descripcion.trim(),
                        monto = montoParsed,
                        estado = "Pendiente",
                        sincronizado = false
                    )
                )
                onVolverClick()
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Nueva Cotización",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = primaryColor
                        )
                        Text(
                            text = "Propuesta comercial para el cliente",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = primaryColor
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { validarYGuardar() }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Guardar Cotización",
                            tint = secondaryColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Encabezado de la tarjeta
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.Create,
                                    contentDescription = null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Detalle de Propuesta",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Describe el servicio y define el valor comercial",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.30f)
                    )

                    // Banner de error animado
                    AnimatedVisibility(
                        visible = !errorMensaje.isNullOrBlank(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        errorMensaje?.let { mensaje ->
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = mensaje,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = {
                            descripcion = it
                            errorMensaje = null
                        },
                        label = { Text("Descripción del proyecto / servicio") },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Edit,
                                contentDescription = null,
                                tint = primaryColor
                            )
                        },
                        minLines = 2,
                        maxLines = 4,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = montoText,
                        onValueChange = { input ->
                            val filtrado = input.filter { it.isDigit() || it == '.' || it == ',' }
                            if (filtrado.count { it == '.' || it == ',' } <= 1) {
                                montoText = filtrado
                                errorMensaje = null
                            }
                        },
                        label = { Text("Monto") },
                        prefix = { Text("$ ") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { validarYGuardar() }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Banner ejecutivo de vista previa del monto
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = primaryColor,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "TOTAL A COTIZAR",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.6.sp,
                                    color = onPrimaryColor.copy(alpha = 0.78f)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = montoValido?.let { formatearVistaPreviaMonto(it) } ?: "$0",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = onPrimaryColor
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(50),
                                color = StatusPendingBg
                            ) {
                                Text(
                                    text = "Estado: Pendiente",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusPendingText,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = { validarYGuardar() },
                        colors = ButtonDefaults.buttonColors(containerColor = secondaryColor),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Guardar Cotización",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}