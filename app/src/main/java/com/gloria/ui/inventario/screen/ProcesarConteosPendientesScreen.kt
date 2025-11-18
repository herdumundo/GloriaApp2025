package com.gloria.ui.inventario.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.gloria.data.entity.api.AuditoriaUsuarioPendiente
import com.gloria.data.entity.api.DetalleArticuloPendiente
import com.gloria.data.entity.api.InventarioPendienteSimultaneo
import com.gloria.ui.inventario.viewmodel.ProcesarConteosPendientesViewModel
import com.gloria.ui.components.ConfirmationDialog

/**
 * Pantalla para procesar conteos pendientes simultáneos
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcesarConteosPendientesScreen(
    navController: NavHostController,
    viewModel: ProcesarConteosPendientesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Cargar inventarios pendientes al iniciar
    LaunchedEffect(Unit) {
        viewModel.loadInventariosPendientes()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    uiState.error != null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Error: ${uiState.error}",
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    viewModel.clearError()
                                    viewModel.loadInventariosPendientes()
                                }
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                    uiState.inventariosPendientes.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay inventarios pendientes",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.inventariosPendientes) { inventario ->
                                InventarioPendienteCard(
                                    inventario = inventario,
                                    onClick = {
                                        viewModel.confirmarConteo(inventario.cabecera.winvdNroInv)
                                    },
                                    onDetalleClick = { seleccionado ->
                                        viewModel.selectInventario(seleccionado)
                                    }
                                )
                            }
                        }
                    }
                }
                
        // Diálogo de detalles
        if (uiState.showDetailDialog && uiState.selectedInventario != null) {
            InventarioPendienteDetailDialog(
                inventario = uiState.selectedInventario!!,
                logsRemotos = uiState.logsRemotos,
                onDismiss = { viewModel.closeDetailDialog() }
            )
        }
    }

    // Diálogo de confirmación
    ConfirmationDialog(
        showDialog = uiState.showConfirmDialog,
        onDismiss = { viewModel.closeConfirmDialog() },
        onConfirm = { viewModel.procesarConfirmacion() },
        title = "Confirmar Conteo",
        message = "¿Está seguro que desea confirmar el conteo del inventario #${uiState.inventarioToConfirm ?: ""}?",
        confirmButtonText = "Confirmar",
        dismissButtonText = "Cancelar",
        isLoading = uiState.isConfirming,
        loadingTitle = "Confirmando...",
        loadingMessage = "Procesando confirmación del conteo...",
        successMessage = uiState.confirmSuccess,
        successTitle = "¡Éxito!",
        successMainMessage = "El conteo ha sido confirmado exitosamente",
        confirmButtonColor = MaterialTheme.colorScheme.primary,
        confirmIconColor = MaterialTheme.colorScheme.primary,
        confirmIcon = Icons.Default.CheckCircle
    )
}


/**
 * Card para mostrar un inventario pendiente
 */
@Composable
private fun InventarioPendienteCard(
    inventario: InventarioPendienteSimultaneo,
    onClick: () -> Unit,
    onDetalleClick: (InventarioPendienteSimultaneo) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header del card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Inventario #${inventario.cabecera.winvdNroInv}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "${inventario.cabecera.fliaDesc} - ${inventario.cabecera.grupDesc}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "${inventario.cabecera.sucursal} - ${inventario.cabecera.deposito}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Área: ${inventario.cabecera.areaDesc}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Artículos: ${inventario.detalleArticulos.size} | Usuarios que contaron: ${inventario.cabecera.cantidadUsuariosContaron}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Contraer" else "Expandir",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Contenido expandible
            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Menús colapsables
                OutlinedButton(
                    onClick = { onDetalleClick(inventario) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ver detalle de artículos",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
              //  AuditoriaUsuariosSection(inventario.auditoriaUsuarios, inventario.detalleArticulos)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Botón de confirmar conteo
                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Confirmar Conteo")
                }
            }
        }
    }
}

/**
 * Diálogo para mostrar los detalles de un inventario pendiente
 */
@Composable
private fun InventarioPendienteDetailDialog(
    inventario: InventarioPendienteSimultaneo,
    logsRemotos: Map<String, List<com.gloria.data.model.ConteosLogPayload>>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Conteo simultáneo  #${inventario.cabecera.winvdNroInv}",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "Artículos: ${inventario.detalleArticulos.size} | Usuarios que contaron: ${inventario.cabecera.cantidadUsuariosContaron}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column {
                 DetalleArticulosSection(
                    detalleArticulos = inventario.detalleArticulos,
                    logsRemotos = logsRemotos,
                    initiallyExpanded = true
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

/**
 * Sección colapsable para mostrar el detalle de artículos
 */
@Composable
private fun DetalleArticulosSection(
    detalleArticulos: List<DetalleArticuloPendiente>,
    logsRemotos: Map<String, List<com.gloria.data.model.ConteosLogPayload>>,
    initiallyExpanded: Boolean = false
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }
    var filtroArticulo by remember { mutableStateOf("") }
    var filtroLote by remember { mutableStateOf("") }
    var filtroDescripcion by remember { mutableStateOf("") }
    var filtroCantidadMayorCero by remember { mutableStateOf(false) }
    
    val articulosFiltrados = remember(
        filtroArticulo,
        filtroLote,
        filtroDescripcion,
        filtroCantidadMayorCero,
        detalleArticulos
    ) {
        detalleArticulos.filter { articulo ->
            val cumpleArticulo = filtroArticulo.isBlank() ||
                articulo.winvdArt.contains(filtroArticulo, ignoreCase = true)
            val cumpleLote = filtroLote.isBlank() ||
                articulo.winvdLote.contains(filtroLote, ignoreCase = true)
            val cumpleDescripcion = filtroDescripcion.isBlank() ||
                articulo.artDesc.contains(filtroDescripcion, ignoreCase = true)
            val cumpleCantidad = !filtroCantidadMayorCero || articulo.cantidadTotalContada > 0

            cumpleArticulo && cumpleLote && cumpleDescripcion && cumpleCantidad
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column {


            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    var filtrosExpandidos by remember { mutableStateOf(false) }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { filtrosExpandidos = !filtrosExpandidos }
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Filtros",
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Filtros de búsqueda",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Icon(
                                imageVector = if (filtrosExpandidos) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (filtrosExpandidos) "Contraer" else "Expandir",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = filtrosExpandidos,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            OutlinedTextField(
                                value = filtroDescripcion,
                                onValueChange = { filtroDescripcion = it },
                                label = { Text("Descripción", style = MaterialTheme.typography.bodySmall) },
                                placeholder = { Text("Buscar descripción...", style = MaterialTheme.typography.bodySmall) },
                                trailingIcon = {
                                    if (filtroDescripcion.isNotEmpty()) {
                                        IconButton(
                                            onClick = { filtroDescripcion = "" },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Clear,
                                                contentDescription = "Limpiar",
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodySmall
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedTextField(
                                    value = filtroArticulo,
                                    onValueChange = { filtroArticulo = it },
                                    label = { Text("Artículo", style = MaterialTheme.typography.bodySmall) },
                                    placeholder = { Text("Código...", style = MaterialTheme.typography.bodySmall) },
                                    trailingIcon = {
                                        if (filtroArticulo.isNotEmpty()) {
                                            IconButton(
                                                onClick = { filtroArticulo = "" },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Clear,
                                                    contentDescription = "Limpiar",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp),
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodySmall
                                )

                                OutlinedTextField(
                                    value = filtroLote,
                                    onValueChange = { filtroLote = it },
                                    label = { Text("Lote", style = MaterialTheme.typography.bodySmall) },
                                    placeholder = { Text("Lote...", style = MaterialTheme.typography.bodySmall) },
                                    trailingIcon = {
                                        if (filtroLote.isNotEmpty()) {
                                            IconButton(
                                                onClick = { filtroLote = "" },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Clear,
                                                    contentDescription = "Limpiar",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp),
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodySmall
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { filtroCantidadMayorCero = !filtroCantidadMayorCero }
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint = if (filtroCantidadMayorCero) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            }
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(
                                                text = "Artículos con conteo > 0",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = "${articulosFiltrados.count { it.cantidadTotalContada > 0 }} artículos cumplen",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Checkbox(
                                        checked = filtroCantidadMayorCero,
                                        onCheckedChange = { filtroCantidadMayorCero = it },
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = {
                                        filtroArticulo = ""
                                        filtroLote = ""
                                        filtroDescripcion = ""
                                        filtroCantidadMayorCero = false
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Limpiar filtros", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val articulosAgrupados = remember(articulosFiltrados) {
                        articulosFiltrados.groupBy { it.winvdArt }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 420.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val hayFiltrosActivos = filtroArticulo.isNotEmpty() ||
                            filtroLote.isNotEmpty() ||
                            filtroDescripcion.isNotEmpty() ||
                            filtroCantidadMayorCero

                        if (articulosFiltrados.isEmpty() && hayFiltrosActivos) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            Icons.Default.Search,
                                            contentDescription = null,
                                            modifier = Modifier.size(48.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "No se encontraron artículos",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Ajusta los filtros aplicados para ver resultados.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        } else {
                            items(articulosAgrupados.entries.toList(), key = { it.key }) { entry ->
                                val listaDetalles = entry.value.sortedBy { it.winvdSecu }
                                val first = listaDetalles.first()

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp)
                                    ) {
                                        var detallesArticuloExpandidos by remember { mutableStateOf(false) }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "${entry.key} - ${first.artDesc}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Text(
                                                    text = "Familia: ${first.fliaDesc} | Grupo: ${first.grupDesc}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            IconButton(onClick = { detallesArticuloExpandidos = !detallesArticuloExpandidos }) {
                                                Icon(
                                                    imageVector = if (detallesArticuloExpandidos) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                    contentDescription = if (detallesArticuloExpandidos) "Colapsar" else "Expandir"
                                                )
                                            }
                                        }

                                        OutlinedCard(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            colors = CardDefaults.outlinedCardColors(
                                                containerColor = MaterialTheme.colorScheme.surface
                                            )
                                        ) {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                ) {
                                                    Text(
                                                        text = "Ord",
                                                        fontWeight = FontWeight.Medium,
                                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                                        modifier = Modifier.weight(0.6f)
                                                    )
                                                    Text(
                                                        text = "Lote",
                                                        fontWeight = FontWeight.Medium,
                                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                                        modifier = Modifier.weight(1.2f)
                                                    )

                                                    Text(
                                                        text = "Conteo",
                                                        fontWeight = FontWeight.Bold,
                                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                                        textAlign = TextAlign.End,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    Text(
                                                        text = "Detalle",
                                                        fontWeight = FontWeight.Medium,
                                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                                        textAlign = TextAlign.End,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                }
                                                Divider(
                                                    thickness = 0.5.dp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                                )

                                                listaDetalles.forEachIndexed { idx, detalle ->
                                                    val detalleKey = "${detalle.winvdSecu}_${detalle.winvdArt}_${detalle.winvdLote}"
                                                    val key = "${detalle.winvdSecu}_${detalle.winvdArt}"
                                                    val logsLinea = logsRemotos[key].orEmpty().sortedBy { it.orden }
                                                    key(detalleKey) {
                                                        var detalleExpandido by remember { mutableStateOf(false) }
                                                        Column(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(vertical = 4.dp)
                                                        ) {
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Text(
                                                                    text = detalle.winvdSecu.toString(),
                                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp),
                                                                    modifier = Modifier.weight(0.6f)
                                                                )
                                                                Text(
                                                                    text = detalle.winvdLote,
                                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp),
                                                                    modifier = Modifier.weight(1.2f)
                                                                )

                                                                Text(
                                                                    text = detalle.cantidadTotalContada.toString(),
                                                                    color = if (detalle.cantidadTotalContada > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                                                    fontWeight = if (detalle.cantidadTotalContada > 0) FontWeight.SemiBold else FontWeight.Normal,
                                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp),
                                                                    textAlign = TextAlign.End,
                                                                    modifier = Modifier.weight(1f)
                                                                )

                                                                Row(
                                                                    modifier = Modifier.weight(1f),
                                                                    horizontalArrangement = Arrangement.End,
                                                                    verticalAlignment = Alignment.CenterVertically
                                                                ) {

                                                                    IconButton(
                                                                        onClick = { detalleExpandido = !detalleExpandido },
                                                                        modifier = Modifier.size(32.dp)
                                                                    ) {
                                                                        Icon(
                                                                            imageVector = if (detalleExpandido) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                                            contentDescription = if (detalleExpandido) "Ocultar detalle" else "Ver detalle"
                                                                        )
                                                                    }
                                                                }
                                                            }

                                                            AnimatedVisibility(visible = detalleExpandido) {
                                                                Column(
                                                                    modifier = Modifier
                                                                        .fillMaxWidth()
                                                                        .padding(start = 4.dp, top = 4.dp),
                                                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                                                ) {
                                                                    
                                                                    
                                                                    Spacer(modifier = Modifier.height(8.dp))
                                                                    
                                                                    if (logsLinea.isEmpty()) {
                                                                        Text(
                                                                            text = "Sin registros remotos.",
                                                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp),
                                                                            modifier = Modifier
                                                                                .fillMaxWidth()
                                                                                .padding(top = 4.dp)
                                                                        )
                                                                    } else {
                                                                        val totalConvertida = logsLinea.sumOf { it.cantidadConvertida }
                                                                        Column(
                                                                            modifier = Modifier
                                                                                .fillMaxWidth()
                                                                                .padding(top = 6.dp)
                                                                        ) {
                                                                            Row(
                                                                                modifier = Modifier.fillMaxWidth(),
                                                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                                                            ) {
                                                                                Text(
                                                                                    text = "Ord",
                                                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                                                                    fontWeight = FontWeight.SemiBold,
                                                                                    modifier = Modifier.weight(0.4f)
                                                                                )
                                                                                Text(
                                                                                    text = "Tipo",
                                                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                                                                    fontWeight = FontWeight.SemiBold,
                                                                                    modifier = Modifier.weight(0.7f)
                                                                                )
                                                                                Text(
                                                                                    text = "Usuario",
                                                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                                                                    fontWeight = FontWeight.SemiBold,
                                                                                    modifier = Modifier.weight(1f)
                                                                                )
                                                                                Text(
                                                                                    text = "Conteo",
                                                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                                                                    fontWeight = FontWeight.SemiBold,
                                                                                    textAlign = TextAlign.End,
                                                                                    modifier = Modifier.weight(0.8f)
                                                                                )
                                                                                Text(
                                                                                    text = "Conv.",
                                                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                                                                    fontWeight = FontWeight.SemiBold,
                                                                                    textAlign = TextAlign.End,
                                                                                    modifier = Modifier.weight(0.9f)
                                                                                )
                                                                            }
                                                                            logsLinea.forEachIndexed { logIdx, log ->
                                                                                Row(
                                                                                    modifier = Modifier
                                                                                        .fillMaxWidth()
                                                                                        .padding(vertical = 2.dp),
                                                                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                                                                ) {
                                                                                    Text(
                                                                                        text = log.orden.toString(),
                                                                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp),
                                                                                        modifier = Modifier.weight(0.4f)
                                                                                    )
                                                                                    Text(
                                                                                        text = if(log.tipo=="CAJAS") {"Caj"} else {"Uni"},
                                                                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp),
                                                                                        modifier = Modifier.weight(0.7f)
                                                                                    )
                                                                                    Text(
                                                                                        text = log.usuario.ifBlank { "-" },
                                                                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp),
                                                                                        modifier = Modifier.weight(1f)
                                                                                    )
                                                                                    Text(
                                                                                        text = log.cantidadIngresada.toString(),
                                                                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp),
                                                                                        textAlign = TextAlign.End,
                                                                                        modifier = Modifier.weight(0.8f)
                                                                                    )
                                                                                    Text(
                                                                                        text = log.cantidadConvertida.toString(),
                                                                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp),
                                                                                        textAlign = TextAlign.End,
                                                                                        modifier = Modifier.weight(0.9f)
                                                                                    )
                                                                                }
                                                                                if (logIdx < logsLinea.lastIndex) {
                                                                                    Divider(
                                                                                        thickness = 0.5.dp,
                                                                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                                                                    )
                                                                                }
                                                                            }
                                                                            Divider(
                                                                                thickness = 0.5.dp,
                                                                                color = MaterialTheme.colorScheme.error
                                                                            )
                                                                            Row(
                                                                                modifier = Modifier
                                                                                    .fillMaxWidth()
                                                                                    .padding(vertical = 2.dp),
                                                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                                                            ) {
                                                                                Text(
                                                                                    text = "Total",
                                                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                                                                    fontWeight = FontWeight.Bold,
                                                                                    modifier = Modifier.weight(2.1f)
                                                                                )
                                                                                Text(
                                                                                    text = totalConvertida.toString(),
                                                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                                                                    fontWeight = FontWeight.Bold,
                                                                                    textAlign = TextAlign.End,
                                                                                    modifier = Modifier.weight(0.9f)
                                                                                )
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        AnimatedVisibility(visible = detallesArticuloExpandidos) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 6.dp),
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = "Código barra:",
                                                        fontWeight = FontWeight.Medium,
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                    Text(
                                                        text = first.codBarra.ifBlank { "-" },
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = "Grupo parcial:",
                                                        fontWeight = FontWeight.Medium,
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                    Text(
                                                        text = first.descGrupoParcial.ifBlank { "-" },
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = "Fecha actualización:",
                                                        fontWeight = FontWeight.Medium,
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                    Text(
                                                        text = first.fechaActualizacion,
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Sección colapsable para mostrar la auditoría de usuarios
 */
@Composable
private fun AuditoriaUsuariosSection(
    auditoriaUsuarios: List<AuditoriaUsuarioPendiente>,
    detalleArticulos: List<DetalleArticuloPendiente>
) {
    var expanded by remember { mutableStateOf(false) }
    var filtroUsuario by remember { mutableStateOf("") }
    var filtroArticulo by remember { mutableStateOf("") }
    var filtroLote by remember { mutableStateOf("") }
    var filtroCantidadMayorCero by remember { mutableStateOf(false) }
    
    val auditoriasFiltradas = remember(filtroUsuario, filtroArticulo, filtroLote, filtroCantidadMayorCero, auditoriaUsuarios) {
        auditoriaUsuarios.filter { auditoria ->
            val cumpleUsuario = filtroUsuario.isBlank() || 
                auditoria.usuarioContador.contains(filtroUsuario, ignoreCase = true)
            val cumpleArticulo = filtroArticulo.isBlank() || 
                auditoria.winvdArt.contains(filtroArticulo, ignoreCase = true)
            val cumpleLote = filtroLote.isBlank() || 
                auditoria.winvdLote.contains(filtroLote, ignoreCase = true)
            val cumpleCantidad = !filtroCantidadMayorCero || auditoria.cantidadContada > 0
            
            cumpleUsuario && cumpleArticulo && cumpleLote && cumpleCantidad
        }
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Auditoría de Usuarios (${auditoriasFiltradas.size}/${auditoriaUsuarios.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Contraer" else "Expandir"
                )
            }
            
            if (expanded) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    // Filtros colapsables
                    var filtrosExpandidos by remember { mutableStateOf(false) }
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { filtrosExpandidos = !filtrosExpandidos }
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Filtros",
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Filtros de búsqueda",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Icon(
                                if (filtrosExpandidos) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (filtrosExpandidos) "Contraer" else "Expandir",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    
                    // Panel de filtros expandible
                    AnimatedVisibility(
                        visible = filtrosExpandidos,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            OutlinedTextField(
                                value = filtroUsuario,
                                onValueChange = { filtroUsuario = it },
                                label = { Text("Usuario", style = MaterialTheme.typography.bodySmall) },
                                placeholder = { Text("Buscar usuario...", style = MaterialTheme.typography.bodySmall) },
                                trailingIcon = {
                                    if (filtroUsuario.isNotEmpty()) {
                                        IconButton(
                                            onClick = { filtroUsuario = "" },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Clear,
                                                contentDescription = "Limpiar",
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodySmall
                            )
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedTextField(
                                    value = filtroArticulo,
                                    onValueChange = { filtroArticulo = it },
                                    label = { Text("Artículo", style = MaterialTheme.typography.bodySmall) },
                                    placeholder = { Text("Código...", style = MaterialTheme.typography.bodySmall) },
                                    trailingIcon = {
                                        if (filtroArticulo.isNotEmpty()) {
                                            IconButton(
                                                onClick = { filtroArticulo = "" },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Clear,
                                                    contentDescription = "Limpiar",
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp),
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodySmall
                                )
                                
                                OutlinedTextField(
                                    value = filtroLote,
                                    onValueChange = { filtroLote = it },
                                    label = { Text("Lote", style = MaterialTheme.typography.bodySmall) },
                                    placeholder = { Text("Lote...", style = MaterialTheme.typography.bodySmall) },
                                    trailingIcon = {
                                        if (filtroLote.isNotEmpty()) {
                                            IconButton(
                                                onClick = { filtroLote = "" },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Clear,
                                                    contentDescription = "Limpiar",
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp),
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodySmall
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Checkbox para filtrar por cantidad mayor a 0
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { filtroCantidadMayorCero = !filtroCantidadMayorCero }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint = if (filtroCantidadMayorCero) 
                                                MaterialTheme.colorScheme.primary 
                                            else 
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(
                                                text = "Solo con cantidad contada",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = "Cantidad > 0",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Checkbox(
                                        checked = filtroCantidadMayorCero,
                                        onCheckedChange = { filtroCantidadMayorCero = it },
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = {
                                        filtroUsuario = ""
                                        filtroArticulo = ""
                                        filtroLote = ""
                                        filtroCantidadMayorCero = false
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "Limpiar filtros",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Mapa código -> descripción desde detalle de artículos
                    val codigoADescripcion = remember(detalleArticulos) { detalleArticulos.associate { it.winvdArt to it.artDesc } }
                    // Lista de auditorías agrupadas por descripción (fallback al código si no hay descripción)
                    val auditoriasAgrupadas = remember(auditoriasFiltradas, detalleArticulos) {
                        auditoriasFiltradas.groupBy { codigoADescripcion[it.winvdArt] ?: it.winvdArt }
                    }
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                    ) {
                        items(auditoriasAgrupadas.entries.toList()) { entry ->
                            val descripcionArticulo = entry.key
                            val listaAud = entry.value
                            OutlinedCard(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.outlinedCardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    // Título del artículo
                                    Text(
                                        text = descripcionArticulo,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    // Encabezados columnas
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Usuario",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.weight(1.2f)
                                        )
                                        Text(
                                            text = "Lote",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.End,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = "Contada",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.End,
                                            modifier = Modifier.weight(0.8f)
                                        )
                                    }
                                    Divider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                                    // Filas de auditoría por artículo
                                    listaAud.forEach { a ->
                                        OutlinedCard(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            colors = CardDefaults.outlinedCardColors(
                                                containerColor = MaterialTheme.colorScheme.surface
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = a.usuarioContador,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    modifier = Modifier.weight(1.2f)
                                                )
                                                Text(
                                                    text = a.winvdLote,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    textAlign = TextAlign.End,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Text(
                                                    text = "${a.cantidadContada}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    textAlign = TextAlign.End,
                                                    modifier = Modifier.weight(0.8f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Item para mostrar un artículo del detalle
 */
@Composable
private fun DetalleArticuloItem(articulo: DetalleArticuloPendiente) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "${articulo.winvdArt} - ${articulo.artDesc}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Lote: ${articulo.winvdLote} | Vto: ${articulo.winvdFecVto}",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Cantidad: ${articulo.cantidadTotalContada}",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Item para mostrar un usuario de la auditoría
 */
@Composable
private fun AuditoriaUsuarioItem(auditoria: AuditoriaUsuarioPendiente) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "Usuario: ${auditoria.usuarioContador}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Artículo: ${auditoria.winvdArt} | Lote: ${auditoria.winvdLote}",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Cantidad: ${auditoria.cantidadContada} | Fecha: ${auditoria.fechaConteo}",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
