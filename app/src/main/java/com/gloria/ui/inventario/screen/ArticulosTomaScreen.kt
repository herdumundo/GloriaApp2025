package com.gloria.ui.inventario.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gloria.ui.components.ArticulosTomaTable
import com.gloria.ui.inventario.viewmodel.ArticulosTomaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticulosTomaScreen(
    nroToma: Int,
    onNavigateBack: () -> Unit,
    viewModel: ArticulosTomaViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Cargar datos cuando la pantalla se compone
    LaunchedEffect(nroToma) {
        viewModel.cargarArticulos(nroToma)
    }


        Box(
            modifier = Modifier
                .fillMaxSize()

        ) {
            when {
                state.isLoading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Card del loading
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Título
                                Text(
                                    text = "Cargando...",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                
                                // Barra de progreso
                                LinearProgressIndicator(
                                    progress = state.loadingProgress / 100f,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp),
                                    color = MaterialTheme.colorScheme.error,
                                    trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                )
                                
                                // Texto descriptivo
                                Text(
                                    text = "Ejecutando consulta Oracle...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                
                                // Contador de progreso
                                Text(
                                    text = "${state.loadingCurrent}/${state.loadingTotal}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
                
                state.error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Error",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.error!!,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { 
                                viewModel.limpiarError()
                                viewModel.cargarArticulos(nroToma)
                            }
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
                
                state.articulos.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No hay artículos en esta toma",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "La toma #$nroToma no contiene artículos registrados",
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Header con información de selección
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                // Primera fila: Información de selección
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Artículos de la toma",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "${state.articulos.count { it.isSelected }} de ${state.articulos.size} seleccionados",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    
                                    Button(
                                        onClick = { viewModel.deseleccionarTodos() },
                                        enabled = state.articulos.any { it.isSelected }
                                    ) {
                                        Text("Limpiar selección")
                                    }
                                }
                                
                                // Segunda fila: Botón de cancelar
                                if (state.articulos.any { it.isSelected }) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (state.articulos.count { it.isSelected } == state.articulos.size) {
                                                "Cancelar toda la toma #$nroToma"
                                            } else {
                                                "Cancelar ${state.articulos.count { it.isSelected }} artículos "
                                            },
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        
                                        Button(
                                            onClick = { viewModel.showConfirmarCancelacionDialog() },
                                            enabled = !state.isLoading,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.error
                                            )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (state.isLoading) "Cancelando..." else "Cancelar",
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        // Tabla de artículos
                        ArticulosTomaTable(
                            articulos = state.articulos,
                            onArticuloClick = { articulo ->
                                viewModel.toggleArticuloSeleccionado(articulo)
                            },
                            onSubgrupoClick = { subgrupo ->
                                viewModel.seleccionarPorSubgrupo(subgrupo)
                            },
                            onGrupoClick = { grupo ->
                                viewModel.seleccionarPorGrupo(grupo)
                            },
                            onFamiliaClick = { familia ->
                                viewModel.seleccionarPorFamilia(familia)
                            },
                            onSelectAllClick = {
                                viewModel.seleccionarTodos()
                            }
                        )
                    }
                }
            }
        }
        
        // Diálogo de confirmación de cancelación (idéntico al de TomaManualScreen)
        if (state.showConfirmarCancelacionDialog) {
            AlertDialog(
                onDismissRequest = { 
                    if (!state.isLoading && state.successMessage == null) {
                        viewModel.hideConfirmarCancelacionDialog()
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Título
                        Text(
                            text = when {
                                state.isLoading -> "Cancelando..."
                                state.successMessage != null -> "¡Éxito!"
                                else -> "Confirmar Cancelación"
                            },
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        // Contenido según el estado
                        when {
                            state.isLoading -> {
                                // Mostrar loading con porcentaje
                                CircularProgressIndicator(
                                    progress = state.loadingProgress / 100f,
                                    modifier = Modifier.size(80.dp),
                                    strokeWidth = 8.dp,
                                    color = MaterialTheme.colorScheme.error
                                )
                                
                                Text(
                                    text = "Procesando... ${state.loadingProgress.toInt()}%",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                
                                Text(
                                    text = if (state.articulos.count { it.isSelected } == state.articulos.size) {
                                        "Cancelando toda la toma #$nroToma"
                                    } else {
                                        "Cancelando ${state.articulos.count { it.isSelected }} artículos seleccionados"
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            state.successMessage != null -> {
                                // Mostrar mensaje de éxito
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                
                                Text(
                                    text = "Cancelación realizada correctamente",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                
                                Text(
                                    text = state.successMessage!!,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                            else -> {
                                // Mostrar mensaje de confirmación
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                                
                                Text(
                                    text = if (state.articulos.count { it.isSelected } == state.articulos.size) {
                                        "¿Está seguro de que desea cancelar toda la toma #$nroToma?"
                                    } else {
                                        "¿Está seguro de que desea cancelar ${state.articulos.count { it.isSelected }} artículos seleccionados?"
                                    },
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    when {
                        state.isLoading -> {
                            // No mostrar botón durante loading
                        }
                        state.successMessage != null -> {
                            TextButton(onClick = { 
                                viewModel.hideConfirmarCancelacionDialog()
                                viewModel.clearSuccessMessage()
                                onNavigateBack() // Navegar de vuelta a la lista de tomas
                            }) {
                                Text("Aceptar")
                            }
                        }
                        else -> {
                            TextButton(onClick = { 
                                viewModel.cancelarSeleccionados(nroToma)
                            }) {
                                Text("Confirmar")
                            }
                        }
                    }
                },
                dismissButton = {
                    if (!state.isLoading && state.successMessage == null) {
                        TextButton(onClick = { viewModel.hideConfirmarCancelacionDialog() }) {
                            Text("Cancelar")
                        }
                    }
                }
            )
        }
    }

