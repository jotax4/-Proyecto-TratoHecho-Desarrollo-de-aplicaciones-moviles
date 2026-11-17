package com.example.casotratohecho.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.casotratohecho.data.Cliente
import com.example.casotratohecho.data.Cotizacion
import com.example.casotratohecho.ui.theme.StatusApprovedBg
import com.example.casotratohecho.ui.theme.StatusApprovedText
import com.example.casotratohecho.ui.theme.StatusPendingBg
import com.example.casotratohecho.ui.theme.StatusPendingText
import com.example.casotratohecho.ui.theme.StatusRejectedBg
import com.example.casotratohecho.ui.theme.StatusRejectedText
import com.example.casotratohecho.viewmodel.ClienteViewModel
import com.example.casotratohecho.viewmodel.CotizacionViewModel
import java.text.NumberFormat
import java.util.Locale

private fun formatearMonto(monto: Double): String {
    val formato = NumberFormat.getNumberInstance(Locale("es", "CL")).apply {
        maximumFractionDigits = if (monto % 1.0 == 0.0) 0 else 2
    }
    return "$${formato.format(monto)}"
}

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
    val montoTotal = remember(cotizaciones) { cotizaciones.sumOf { it.monto } }

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Ficha de Cliente",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = primaryColor
                        )
                        Text(
                            text = "Perfil y propuestas comerciales",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAgregarCotizacionClick,
                shape = RoundedCornerShape(16.dp),
                containerColor = secondaryColor,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                icon = {
                    Icon(Icons.Filled.Add, contentDescription = "Nueva Cotización")
                },
                text = {
                    Text(
                        text = "Nueva Cotización",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 92.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Tarjeta de perfil del cliente
            cliente?.let { cli ->
                item {
                    ClientePerfilCard(
                        cliente = cli,
                        onLlamarClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${cli.telefono.replace(" ", "")}")
                            }
                            runCatching { context.startActivity(intent) }
                        },
                        onCorreoClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:${cli.correo.trim()}")
                            }
                            runCatching { context.startActivity(intent) }
                        }
                    )
                }
            }

            // 2. Banner ejecutivo de resumen financiero
            item {
                ResumenCotizacionesCard(
                    cantidad = cotizaciones.size,
                    montoTotal = montoTotal
                )
            }

            // 3. Encabezado de sección
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Historial de Cotizaciones",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (cotizaciones.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "${cotizaciones.size} ${if (cotizaciones.size == 1) "registro" else "registros"}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // 4. Lista o estado vacío de cotizaciones
            if (cotizaciones.isEmpty()) {
                item {
                    EmptyCotizacionesState(onAgregarCotizacionClick = onAgregarCotizacionClick)
                }
            } else {
                items(cotizaciones, key = { it.id }) { cotizacion ->
                    CotizacionCard(cotizacion = cotizacion)
                }
            }
        }
    }
}

@Composable
private fun ClientePerfilCard(
    cliente: Cliente,
    onLlamarClick: () -> Unit,
    onCorreoClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val iniciales = remember(cliente.nombre) {
        cliente.nombre
            .trim()
            .split(" ")
            .filter { it.isNotEmpty() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
            .ifEmpty { "?" }
    }

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
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(58.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = iniciales,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = primaryColor
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = cliente.nombre,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (cliente.empresa.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = cliente.empresa,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (cliente.rut.isNotBlank()) {
                    DatoClienteRow(
                        icono = Icons.Outlined.AccountBox,
                        etiqueta = "RUT",
                        valor = cliente.rut
                    )
                }
                if (cliente.telefono.isNotBlank()) {
                    DatoClienteRow(
                        icono = Icons.Outlined.Phone,
                        etiqueta = "Teléfono",
                        valor = cliente.telefono
                    )
                }
                if (cliente.correo.isNotBlank()) {
                    DatoClienteRow(
                        icono = Icons.Outlined.Email,
                        etiqueta = "Correo",
                        valor = cliente.correo
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onLlamarClick,
                    enabled = cliente.telefono.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Phone,
                        contentDescription = "Llamar",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Llamar",
                        fontWeight = FontWeight.Bold
                    )
                }

                if (cliente.correo.isNotBlank()) {
                    OutlinedButton(
                        onClick = onCorreoClick,
                        border = BorderStroke(1.2.dp, secondaryColor),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = secondaryColor),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Email,
                            contentDescription = "Enviar correo",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Correo",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DatoClienteRow(
    icono: ImageVector,
    etiqueta: String,
    valor: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(30.dp),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "$etiqueta: ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ResumenCotizacionesCard(
    cantidad: Int,
    montoTotal: Double
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = primaryColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            primaryColor,
                            primaryColor.copy(alpha = 0.85f)
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TOTAL COTIZADO",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp,
                        color = onPrimaryColor.copy(alpha = 0.78f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formatearMonto(montoTotal),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = onPrimaryColor
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = onPrimaryColor.copy(alpha = 0.14f)
                ) {
                    Text(
                        text = "$cantidad ${if (cantidad == 1) "cotización" else "cotizaciones"}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = onPrimaryColor,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyCotizacionesState(onAgregarCotizacionClick: () -> Unit) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Create,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Sin cotizaciones aún",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Agrega una propuesta comercial para llevar el seguimiento de este cliente.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
            )

            Button(
                onClick = onAgregarCotizacionClick,
                colors = ButtonDefaults.buttonColors(containerColor = secondaryColor),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.height(46.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Crear cotización",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CotizacionCard(cotizacion: Cotizacion) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val estadoNormalizado = cotizacion.estado.trim().lowercase()

    val (badgeBg, badgeText) = when {
        estadoNormalizado.contains("aproba") || estadoNormalizado.contains("acepta") -> {
            StatusApprovedBg to StatusApprovedText
        }
        estadoNormalizado.contains("rechaza") || estadoNormalizado.contains("cancela") -> {
            StatusRejectedBg to StatusRejectedText
        }
        else -> {
            StatusPendingBg to StatusPendingText
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = cotizacion.descripcion,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                )

                Surface(
                    shape = RoundedCornerShape(50),
                    color = badgeBg
                ) {
                    Text(
                        text = cotizacion.estado,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = badgeText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.30f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatearMonto(cotizacion.monto),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryColor
                )

                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (cotizacion.sincronizado) StatusApprovedBg else StatusPendingBg
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (cotizacion.sincronizado) {
                                Icons.Outlined.CheckCircle
                            } else {
                                Icons.Outlined.Refresh
                            },
                            contentDescription = null,
                            tint = if (cotizacion.sincronizado) StatusApprovedText else StatusPendingText,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (cotizacion.sincronizado) "Sincronizado" else "Pendiente",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (cotizacion.sincronizado) StatusApprovedText else StatusPendingText
                        )
                    }
                }
            }
        }
    }
}