package com.gloria.ui.informe.screen

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.gloria.data.model.ConteoPendienteResponse
import com.gloria.data.model.InventarioConteo
import com.gloria.ui.informe.viewmodel.InformeConteosPendientesViewModel
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformeConteosPendientesScreen(
    navController: NavHostController,
    viewModel: InformeConteosPendientesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var fechaSeleccionada by remember { 
        mutableStateOf(
            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 12) // Mediodía para evitar problemas de zona horaria
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
        )
    }
    var mostrarDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
         
        // Campo de fecha y botón buscar
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
                Text(
                    text = "Seleccionar Fecha",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Campo de fecha
                OutlinedTextField(
                    value = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fechaSeleccionada),
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Fecha") },
                    trailingIcon = {
                        IconButton(onClick = { mostrarDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Botón buscar
                Button(
                    onClick = { viewModel.buscarConteosPendientes(fechaSeleccionada) },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Buscando...")
                    } else {
                        Icon(Icons.Default.Search, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Buscar")
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Contenido principal
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Consultando conteos pendientes...")
                    }
                }
            }
            
            uiState.errorMessage != null -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Error",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiState.errorMessage ?: "Error desconocido",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.limpiarError() }
                        ) {
                            Text("Aceptar")
                        }
                    }
                }
            }
            
            uiState.conteosPendientes != null -> {
                // Mostrar resultados
                ConteosPendientesResultados(
                    conteosPendientes = uiState.conteosPendientes!!,
                    onDetalleClick = { viewModel.mostrarDetalleInventario(it) }
                )
            }
            
            else -> {
                // Estado inicial
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Selecciona una fecha y presiona 'Buscar' para consultar los conteos pendientes",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
    
    // Date Picker
    if (mostrarDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = fechaSeleccionada.time
        )
        
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            Log.d("DATE_PICKER_DEBUG", "🔍 Milisegundos recibidos: $millis")
                            
                            // Crear una nueva fecha usando solo año, mes y día
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = millis
                            Log.d("DATE_PICKER_DEBUG", "🔍 Calendar original - Año: ${calendar.get(Calendar.YEAR)}, Mes: ${calendar.get(Calendar.MONTH)}, Día: ${calendar.get(Calendar.DAY_OF_MONTH)}")
                            
                            // Crear un nuevo Calendar con la fecha local y SUMAR UN DÍA
                            val localCalendar = Calendar.getInstance()
                            localCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
                            localCalendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
                            localCalendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
                            localCalendar.set(Calendar.HOUR_OF_DAY, 12) // Establecer a mediodía para evitar problemas de zona horaria
                            localCalendar.set(Calendar.MINUTE, 0)
                            localCalendar.set(Calendar.SECOND, 0)
                            localCalendar.set(Calendar.MILLISECOND, 0)
                            
                            // SUMAR UN DÍA para compensar el problema de zona horaria
                            localCalendar.add(Calendar.DAY_OF_MONTH, 1)
                            
                            Log.d("DATE_PICKER_DEBUG", "🔍 Calendar local (después de sumar 1 día) - Año: ${localCalendar.get(Calendar.YEAR)}, Mes: ${localCalendar.get(Calendar.MONTH)}, Día: ${localCalendar.get(Calendar.DAY_OF_MONTH)}")
                            
                            val nuevaFecha = localCalendar.time
                            Log.d("DATE_PICKER_DEBUG", "🔍 Fecha final: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(nuevaFecha)}")
                            
                            fechaSeleccionada = nuevaFecha
                        }
                        mostrarDatePicker = false
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    // Diálogo de detalle
    if (uiState.mostrarDetalle && uiState.detalleSeleccionado != null) {
        DetalleInventarioDialog(
            inventario = uiState.detalleSeleccionado!!,
            onDismiss = { viewModel.ocultarDetalleInventario() }
        )
    }
}

@Composable
fun ConteosPendientesResultados(
    conteosPendientes: ConteoPendienteResponse,
    onDetalleClick: (InventarioConteo) -> Unit
) {
    // Lista scrolleable de inventarios
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(conteosPendientes.inventories) { inventario ->
            InventarioCard(
                inventario = inventario,
                onDetalleClick = { onDetalleClick(inventario) }
            )
        }
    }
}

@Composable
fun InventarioCard(
    inventario: InventarioConteo,
    onDetalleClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Inventario #${inventario.header.winvdNroInv}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Usuario: ${inventario.header.winveLogin}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = "Sucursal: ${inventario.header.sucursal}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = "Registros: ${inventario.totalRecords}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Button(
                    onClick = onDetalleClick,
                    modifier = Modifier.height(40.dp)
                ) {
                    Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ver Detalle", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun DetalleInventarioDialog(
    inventario: InventarioConteo,
    onDismiss: () -> Unit
) {
    var textoBusqueda by remember { mutableStateOf("") }
    val detallesFiltrados = remember(textoBusqueda, inventario.details) {
        if (textoBusqueda.isBlank()) {
            inventario.details
        } else {
            inventario.details.filter { detalle ->
                detalle.artDesc.contains(textoBusqueda, ignoreCase = true) ||
                detalle.winvdArt.contains(textoBusqueda, ignoreCase = true) ||
                detalle.winvdLote.contains(textoBusqueda, ignoreCase = true) ||
                detalle.fliaDesc.contains(textoBusqueda, ignoreCase = true) ||
                detalle.grupDesc.contains(textoBusqueda, ignoreCase = true) ||
                detalle.codBarra.contains(textoBusqueda, ignoreCase = true) ||
                detalle.winvdCantAct.toString().contains(textoBusqueda, ignoreCase = true) ||
                detalle.winvdCantInv.toString().contains(textoBusqueda, ignoreCase = true)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Detalle del Inventario #${inventario.header.winvdNroInv}")
                Text(
                    text = "Total: ${detallesFiltrados.size} de ${inventario.details.size} productos",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column {
                // Campo de búsqueda
                OutlinedTextField(
                    value = textoBusqueda,
                    onValueChange = { textoBusqueda = it },
                    label = { Text("Buscar productos...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    },
                    trailingIcon = {
                        if (textoBusqueda.isNotEmpty()) {
                            IconButton(onClick = { textoBusqueda = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Lista de productos filtrados
                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp)
                ) {
                    if (detallesFiltrados.isEmpty() && textoBusqueda.isNotEmpty()) {
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
                                        text = "No se encontraron productos",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "Intenta con otros términos de búsqueda",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(detallesFiltrados) { detalle ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Text(
                                        text = detalle.artDesc,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Artículo:",
                                            fontWeight = FontWeight.Medium,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = detalle.winvdArt,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Cant. Actual:",
                                            fontWeight = FontWeight.Medium,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = "${detalle.winvdCantAct}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Cant. Inventario:",
                                            fontWeight = FontWeight.Medium,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = "${detalle.winvdCantInv}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Lote:",
                                            fontWeight = FontWeight.Medium,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = detalle.winvdLote,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Familia:",
                                            fontWeight = FontWeight.Medium,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = detalle.fliaDesc,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Grupo:",
                                            fontWeight = FontWeight.Medium,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = detalle.grupDesc,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Código Barras:",
                                            fontWeight = FontWeight.Medium,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = detalle.codBarra,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}