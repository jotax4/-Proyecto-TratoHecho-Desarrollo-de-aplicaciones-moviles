package com.example.casotratohecho.ui

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Patterns
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
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.casotratohecho.data.Cliente
import com.example.casotratohecho.viewmodel.ClienteViewModel

private fun vibrarDispositivo(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator.vibrate(
            VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
        )
    } else {
        @Suppress("DEPRECATION")
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        @Suppress("DEPRECATION")
        vibrator.vibrate(100)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioClienteScreen(
    viewModel: ClienteViewModel,
    onVolverClick: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var nombre by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var empresa by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf<String?>(null) }

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val rutRegex = Regex("^[0-9]{1,2}\\.?[0-9]{3}\\.?[0-9]{3}-[0-9kK]{1}$")

    fun validarYGuardar() {
        focusManager.clearFocus()
        val rutLimpio = rut.trim().replace(" ", "")
        val telefonoLimpio = telefono.trim().filter { it.isDigit() }

        when {
            nombre.isBlank() -> {
                errorMensaje = "El nombre es obligatorio."
            }
            rutLimpio.isBlank() -> {
                errorMensaje = "El RUT es obligatorio."
            }
            !rutRegex.matches(rutLimpio) -> {
                errorMensaje = "Formato de RUT inválido (Ej: 12.345.678-K)."
            }
            empresa.isBlank() -> {
                errorMensaje = "La empresa es obligatoria."
            }
            telefonoLimpio.length < 8 -> {
                errorMensaje = "Ingrese un número de teléfono válido (mínimo 8 dígitos)."
            }
            correo.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches() -> {
                errorMensaje = "Ingrese un correo electrónico válido."
            }
            else -> {
                errorMensaje = null
                val telefonoFinal = if (telefonoLimpio.length == 9 && telefonoLimpio.startsWith("9")) {
                    "+56 $telefonoLimpio"
                } else {
                    "+56 9 $telefonoLimpio"
                }

                vibrarDispositivo(context)
                viewModel.agregarCliente(
                    Cliente(
                        nombre = nombre.trim(),
                        rut = rutLimpio.uppercase(),
                        empresa = empresa.trim(),
                        telefono = telefonoFinal,
                        correo = correo.trim(),
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
                            text = "Nuevo Cliente",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = primaryColor
                        )
                        Text(
                            text = "Registro comercial en cartera",
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
                            contentDescription = "Guardar Cliente",
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
                    verticalArrangement = Arrangement.spacedBy(14.dp)
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
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Datos del Cliente",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Completa la información de contacto",
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
                        value = nombre,
                        onValueChange = {
                            nombre = it
                            errorMensaje = null
                        },
                        label = { Text("Nombre Completo") },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Person,
                                contentDescription = null,
                                tint = primaryColor
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = rut,
                        onValueChange = { input ->
                            rut = input.filter {
                                it.isDigit() || it == '.' || it == '-' || it == 'k' || it == 'K'
                            }
                            errorMensaje = null
                        },
                        label = { Text("RUT (Ej: 12.345.678-K)") },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.AccountBox,
                                contentDescription = null,
                                tint = primaryColor
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = empresa,
                        onValueChange = {
                            empresa = it
                            errorMensaje = null
                        },
                        label = { Text("Empresa") },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Home,
                                contentDescription = null,
                                tint = primaryColor
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { input ->
                            telefono = input.filter { it.isDigit() }.take(9)
                            errorMensaje = null
                        },
                        label = { Text("Teléfono (9 dígitos)") },
                        prefix = { Text("+56 ") },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Phone,
                                contentDescription = null,
                                tint = primaryColor
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = correo,
                        onValueChange = {
                            correo = it
                            errorMensaje = null
                        },
                        label = { Text("Correo Electrónico") },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Email,
                                contentDescription = null,
                                tint = primaryColor
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { validarYGuardar() }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

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
                            text = "Guardar Cliente",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}