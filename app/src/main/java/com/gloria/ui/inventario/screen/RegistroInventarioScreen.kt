package com.gloria.ui.inventario.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.navigation.NavHostController
import com.gloria.ui.components.InventarioCard
import com.gloria.ui.inventario.viewmodel.RegistroInventarioViewModel

/**
 * Pantalla de Registro de Inventario
 * Muestra los inventarios sincronizados en cards verticales
 */
@Composable
fun RegistroInventarioScreen(
    viewModel: RegistroInventarioViewModel,
    onNavigateToConteo: (Int) -> Unit = {},
    navController: NavHostController
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
                        text = "Los inventarios aparecerán aquí después de sincronizar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Mostrar progreso de sincronización si está sincronizando
                    if (uiState.isSincronizando) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = uiState.syncMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                        }
                        } else {
                            Button(
                                onClick = { viewModel.refreshInventarios() },
                                enabled = !uiState.isSincronizando,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                if (uiState.isSincronizando) {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Sincronizando...",
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                } else {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp),
                                            tint = MaterialTheme.colorScheme.onPrimary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Sincronizar Inventarios",
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                }
            }
            
            else -> {
                // Lista de inventarios
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Botón de sincronización en la parte superior
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { viewModel.refreshInventarios() },
                            enabled = !uiState.isSincronizando,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            if (uiState.isSincronizando) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Sincronizando...",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Sincronizar",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                        
                        // Mostrar mensaje de progreso si está sincronizando
                        if (uiState.isSincronizando) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.syncMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        itemsIndexed(
                            items = uiState.inventarios,
                            key = { index, inventario -> "${inventario.winvd_nro_inv}_$index" }
                        ) { index, inventario ->
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
}


