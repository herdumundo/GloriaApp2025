package com.gloria.ui.inventario.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gloria.ui.components.InventarioCard
import com.gloria.ui.inventario.viewmodel.RegistroInventarioViewModel

/**
 * Pantalla de Registro de Inventario
 * Muestra los inventarios sincronizados en cards verticales
 */
@Composable
fun RegistroInventarioScreen(
    viewModel: RegistroInventarioViewModel,
    onNavigateToConteo: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Cargar usuario logueado al iniciar la pantalla
    LaunchedEffect(Unit) {
        viewModel.cargarUsuarioLogueado()
    }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            uiState.isLoading -> {
                // Estado de carga
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Cargando inventarios...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            uiState.inventarios.isEmpty() -> {
                // Estado vacío
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Sin inventarios",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No hay inventarios registrados",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Los inventarios aparecerán aquí después de sincronizar desde Oracle",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.refreshInventarios() }
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
            
            else -> {
                // Lista de inventarios
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = uiState.inventarios,
                        key = { it.winvd_nro_inv }
                    ) { inventario ->
                        InventarioCard(
                            inventario = inventario,
                            onMenuClick = {
                                // TODO: Mostrar menú de opciones para el inventario
                            },
                            onCheckClick = {
                                // TODO: Marcar como verificado
                            },
                            onCardClick = {
                                onNavigateToConteo(inventario.winvd_nro_inv)
                            }
                        )
                    }
                }
        }
        

    }
}
}


