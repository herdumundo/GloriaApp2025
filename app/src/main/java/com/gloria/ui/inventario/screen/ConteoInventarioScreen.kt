package com.gloria.ui.inventario.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.gloria.ui.components.ArticuloConteoCard
import com.gloria.ui.components.ExitConfirmationDialog
import com.gloria.ui.inventario.viewmodel.ConteoInventarioViewModel

/**
 * Pantalla de Conteo de Inventario
 * Permite realizar el conteo físico de los artículos de un inventario
 */
@Composable
fun ConteoInventarioScreen(
    nroInventario: Int,
    viewModel: ConteoInventarioViewModel,
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsState()
    var showExitDialog by remember { mutableStateOf(false) }
    
    // Manejar el botón atrás
    BackHandler {
        showExitDialog = true
    }
    
    // Cargar artículos al iniciar la pantalla
    LaunchedEffect(nroInventario) {
        viewModel.cargarArticulosInventario(nroInventario)
    }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Barra de búsqueda y botón REGISTRAR
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
                    // Campo de búsqueda
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.actualizarBusqueda(it) },
                            modifier = Modifier.weight(1f),
                            placeholder = { 
                                Text(
                                    text = "Buscar por cualquier campo",
                                    fontSize = 12.sp
                                ) 
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            trailingIcon = {
                                if (uiState.searchQuery.isNotEmpty()) {
                                    IconButton(
                                        onClick = { viewModel.limpiarBusqueda() }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Limpiar búsqueda",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Icono de búsqueda
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar producto",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    // Información de resultados
                    if (uiState.searchQuery.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Resultados: ${viewModel.getArticulosFiltradosCount()} de ${uiState.totalArticulos}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            TextButton(
                                onClick = { viewModel.limpiarBusqueda() }
                            ) {
                                Text(
                                    text = "Limpiar búsqueda",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Botón REGISTRAR
                    Button(
                        onClick = { 
                            viewModel.validarRegistro()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8B0000) // Rojo oscuro
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "REGISTRAR",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Título de la sección
            Text(
                text = "Ingrese las cantidades",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            when {
                uiState.isLoading -> {
                    // Estado de carga
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Cargando artículos...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                uiState.articulos.isEmpty() -> {
                    // Estado vacío
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Sin artículos",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No hay artículos en este inventario",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Verifica que el inventario tenga artículos sincronizados",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { viewModel.refreshArticulos() }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Refrescar")
                            }
                        }
                    }
                }
                
                else -> {
                    // Verificar si hay resultados de búsqueda
                    val articulosFiltrados = viewModel.getArticulosFiltrados()
                    
                    if (uiState.searchQuery.isNotEmpty() && articulosFiltrados.isEmpty()) {
                        // No hay resultados de búsqueda
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Sin resultados",
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No se encontraron resultados",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Intenta con otros términos de búsqueda",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = { viewModel.limpiarBusqueda() }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Limpiar búsqueda")
                                }
                            }
                        }
                    } else {
                        // Lista de artículos
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(
                                items = articulosFiltrados,
                                key = { it.winvdSecu }
                            ) { articulo ->
                                ArticuloConteoCard(
                                    articulo = articulo,
                                    estadoConteo = viewModel.obtenerEstadoConteo(articulo.winvdSecu),
                                    onCajasChanged = { cajas ->
                                        // TODO: Actualizar cantidad de cajas
                                    },
                                    onUnidadesChanged = { unidades ->
                                        // TODO: Actualizar cantidad de unidades
                                    },
                                    onArticuloContado = { articuloId, cantidad ->
                                        viewModel.marcarArticuloContado(articuloId, cantidad)
                                    },
                                    onEstadoConteoChanged = { estado ->
                                        viewModel.actualizarEstadoConteo(articulo.winvdSecu, estado)
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            // Barra de resumen en la parte inferior
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total de artículos: ${uiState.totalArticulos}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = "Cantidad total: ${uiState.cantidadTotal}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
        }
    }
    
    // Alerta de productos no contados
    if (uiState.showAlertNoContados) {
        AlertDialog(
            onDismissRequest = { viewModel.cerrarAlertNoContados() },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Advertencia",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Productos no contados",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            text = {
                val articulosNoContados = viewModel.getArticulosNoContados()
                Column {
                    Text(
                        text = "No has contado todos los productos. Faltan ${articulosNoContados.size} productos por contar:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (articulosNoContados.isNotEmpty()) {
                        Text(
                            text = articulosNoContados.take(3).joinToString("\n") { 
                                "${it.winvdArt} - ${it.artDesc}" 
                            } + if (articulosNoContados.size > 3) "\n..." else "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                Row {
                    TextButton(
                        onClick = { viewModel.cerrarAlertNoContados() }
                    ) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { viewModel.confirmarRegistroSinContarTodo() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8B0000)
                        )
                    ) {
                        Text("Registrar igual")
                    }
                }
            }
        )
    }
    
    // Alerta de confirmación final
    if (uiState.showConfirmRegistro) {
        AlertDialog(
            onDismissRequest = { viewModel.cancelarRegistro() },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Confirmación",
                        tint = Color(0xFF8B0000),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Confirmar registro",
                        color = Color(0xFF8B0000)
                    )
                }
            },
            text = {
                val articulosNoContados = viewModel.getArticulosNoContados()
                Column {
                    Text(
                        text = "¿Estás seguro de que deseas registrar el inventario sin contar todos los productos?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Los productos no contados se registrarán con cantidad 0.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmarYProcesarRegistro() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B0000)
                    )
                ) {
                    Text("Sí, registrar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.cancelarRegistro() }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Diálogo de confirmación de salida
    ExitConfirmationDialog(
        showDialog = showExitDialog,
        onDismiss = { showExitDialog = false },
        navController = navController,
        route = "menu_principal",
        title = "Salir del conteo",
        message = "¿Estás seguro de que deseas salir del conteo de inventario?",
        warningMessage = "Los cambios no guardados se perderán.",
        confirmButtonText = "Sí, salir",
        dismissButtonText = "Cancelar"
    )
    } }
