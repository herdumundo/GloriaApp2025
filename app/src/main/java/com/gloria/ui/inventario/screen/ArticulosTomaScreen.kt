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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Artículos de la toma #$nroToma",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    // Botón para seleccionar/deseleccionar todos
                    IconButton(
                        onClick = { viewModel.seleccionarTodos() }
                    ) {
                        Icon(
                            imageVector = if (state.articulos.all { it.isSelected }) 
                                Icons.Default.Check else Icons.Default.Close,
                            contentDescription = "Seleccionar todos"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
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
    }
}
