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
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
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
                                                "Cancelar ${state.articulos.count { it.isSelected }} artículos seleccionados"
                                            },
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        
                                        Button(
                                            onClick = { viewModel.cancelarSeleccionados(nroToma) },
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
        
        // Diálogo de confirmación de cancelación exitosa
        if (state.showConfirmacionDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.cerrarConfirmacionDialog() },
                title = {
                    Text(
                        text = "Cancelación Exitosa",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = if (state.articulos.count { it.isSelected } == state.articulos.size) {
                            "La toma #$nroToma ha sido cancelada completamente."
                        } else {
                            "Los artículos seleccionados han sido cancelados exitosamente."
                        },
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { 
                            viewModel.cerrarConfirmacionDialog()
                            onNavigateBack()
                        }
                    ) {
                        Text("Aceptar")
                    }
                }
            )
        }
    }

