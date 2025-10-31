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
import androidx.compose.ui.window.Dialog
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
    onClick: () -> Unit
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
                DetalleArticulosSection(inventario.detalleArticulos)
                Spacer(modifier = Modifier.height(8.dp))
                AuditoriaUsuariosSection(inventario.auditoriaUsuarios)
                
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
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Detalles del Conteo",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onDismiss) {
                        Text("Cerrar")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Menús colapsables
                DetalleArticulosSection(inventario.detalleArticulos)
                Spacer(modifier = Modifier.height(8.dp))
                AuditoriaUsuariosSection(inventario.auditoriaUsuarios)
            }
        }
    }
}

/**
 * Sección colapsable para mostrar el detalle de artículos
 */
@Composable
private fun DetalleArticulosSection(detalleArticulos: List<DetalleArticuloPendiente>) {
    var expanded by remember { mutableStateOf(false) }
    var filtroArticulo by remember { mutableStateOf("") }
    var filtroLote by remember { mutableStateOf("") }
    var filtroDescripcion by remember { mutableStateOf("") }
    var filtroCantidadMayorCero by remember { mutableStateOf(false) }
    
    val articulosFiltrados = remember(filtroArticulo, filtroLote, filtroDescripcion, filtroCantidadMayorCero, detalleArticulos) {
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Detalle de Artículos (${articulosFiltrados.size}/${detalleArticulos.size})",
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
                                value = filtroDescripcion,
                                onValueChange = { filtroDescripcion = it },
                                label = { Text("Descripción", style = MaterialTheme.typography.bodySmall) },
                                placeholder = { Text("Buscar...", style = MaterialTheme.typography.bodySmall) },
                                trailingIcon = {
                                    if (filtroDescripcion.isNotEmpty()) {
                                        IconButton(
                                            onClick = { filtroDescripcion = "" },
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
                                    Text(
                                        "Limpiar filtros",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Lista de artículos filtrados
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(articulosFiltrados) { articulo ->
                            DetalleArticuloItem(articulo)
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
private fun AuditoriaUsuariosSection(auditoriaUsuarios: List<AuditoriaUsuarioPendiente>) {
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
                    
                    // Lista de auditorías filtradas
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(auditoriasFiltradas) { auditoria ->
                            AuditoriaUsuarioItem(auditoria)
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
