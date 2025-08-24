package com.gloria.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.gloria.data.model.ArticuloLote

// Componente reutilizable para "Seleccionar Todo" con diseño consistente
@Composable
private fun SeleccionarTodoCard(
    isSelected: Boolean,
    onToggle: (Boolean) -> Unit,
    label: String,
    description: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onToggle(!isSelected)
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE8F5E8) else Color(0xFFFFFFFF)
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) Color(0xFF4CAF50) else Color(0xFFE0E0E0)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle(it) },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF4CAF50),
                    uncheckedColor = Color(0xFF666666)
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color(0xFF2E7D32) else Color(0xFF333333)
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = if (isSelected) Color(0xFF2E7D32) else Color(0xFF666666)
                )
            }
        }
    }
}

@Composable
fun ArticulosLotesDialog(
    articulosLotes: List<ArticuloLote>,
    selectedArticulosLotes: List<ArticuloLote>,
    onDismiss: () -> Unit,
    onConfirm: (List<ArticuloLote>) -> Unit,
    onToggleArticulo: (ArticuloLote) -> Unit,
    onSelectAll: () -> Unit,
    onClearSelection: () -> Unit,
    onSelectSpecific: (List<ArticuloLote>) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var includeZeroStock by remember { mutableStateOf(false) }
    var includeLote by remember { mutableStateOf(true) } // Por defecto seleccionado
    var showConfirmStockCeroDialog by remember { mutableStateOf(false) }
    
    val filteredArticulos = remember(articulosLotes, searchQuery, includeZeroStock, includeLote) {
        articulosLotes.filter { articulo ->
            val matchesSearch = articulo.artDesc.contains(searchQuery, ignoreCase = true) ||
                    articulo.ardeLote.contains(searchQuery, ignoreCase = true) ||
                    articulo.artCodigo.contains(searchQuery, ignoreCase = true)
            
            val matchesStockFilter = includeZeroStock || articulo.cantidad > 0.0
            
            val matchesLoteFilter = if (includeLote) {
                true // Incluir todos los lotes
            } else {
                // Solo incluir artículos con lote por defecto
                articulo.ardeLote == "000000" // && articulo.ardeFecVtoLote == "31-12-5000"
            }
            
            matchesSearch && matchesStockFilter && matchesLoteFilter
        }
    }
    
    // Estado interno para controlar el checkbox "Todos los artículos"
    var isSelectAllChecked by remember { mutableStateOf(false) }
    
    // Función local para seleccionar solo los artículos VISIBLES
    fun selectAllVisible() {
        // Seleccionar SOLO los artículos que están actualmente visibles según el filtro
        // Esto asegura que solo se seleccionen los 6 artículos visibles, no los 83 totales
        val articulosVisibles = filteredArticulos
        
        // Crear una lista temporal de artículos ya seleccionados que NO están en la lista filtrada
        // (para mantener selecciones de artículos que pueden estar ocultos por otros filtros)
        val articulosYaSeleccionados = selectedArticulosLotes.filter { articulo ->
            !filteredArticulos.contains(articulo)
        }
        
        // Combinar: artículos ya seleccionados + todos los artículos visibles
        val nuevaSeleccion = articulosYaSeleccionados + articulosVisibles
        
        // Llamar a la función que selecciona artículos específicos
        onSelectSpecific(nuevaSeleccion)
    }
    
    // Sincronizar el estado del checkbox cuando cambien las selecciones
    LaunchedEffect(selectedArticulosLotes, filteredArticulos) {
        val shouldBeChecked = selectedArticulosLotes.size == filteredArticulos.size && filteredArticulos.isNotEmpty()
        if (isSelectAllChecked != shouldBeChecked) {
            isSelectAllChecked = shouldBeChecked
        }
    }
    
    // Limpiar selección cuando cambie el filtro de stock cero
    LaunchedEffect(includeZeroStock) {
        // Limpiar selección cuando cambie el filtro para evitar inconsistencias
        onClearSelection()
        // Resetear el estado del checkbox
        isSelectAllChecked = false
    }
    
    // Limpiar selección cuando cambie el filtro de lote
    LaunchedEffect(includeLote) {
        // Limpiar selección cuando cambie el filtro para evitar inconsistencias
        onClearSelection()
        // Resetear el estado del checkbox
        isSelectAllChecked = false
    }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Artículos con Lotes",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Barra de búsqueda
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar artículos...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4CAF50),
                        unfocusedBorderColor = Color(0xFF4CAF50).copy(alpha = 0.5f)
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Filtros y contador
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Primera fila: Título del switch
                    Text(
                        text = "Incluir stock cero",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Segunda fila: Switch y contador
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                                                    // Switch para incluir stock cero
                            Switch(
                                checked = includeZeroStock,
                                onCheckedChange = { 
                                    if (it && !includeZeroStock) {
                                        // Mostrar confirmación solo cuando se activa
                                        showConfirmStockCeroDialog = true
                                    } else if (!it) {
                                        // Desactivar directamente y limpiar selección
                                        includeZeroStock = false
                                        // Limpiar selección cuando cambie el filtro
                                        onClearSelection()
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF4CAF50),
                                    checkedTrackColor = Color(0xFF4CAF50).copy(alpha = 0.5f),
                                    uncheckedThumbColor = Color(0xFF666666),
                                    uncheckedTrackColor = Color(0xFFE0E0E0)
                                ),
                                modifier = Modifier.size(32.dp)
                            )
                        
                        // Contador compacto
                        Text(
                            text = "${selectedArticulosLotes.size}/${filteredArticulos.size}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Checkbox para incluir lote
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = includeLote,
                                onCheckedChange = { includeLote = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF4CAF50),
                                    uncheckedColor = Color(0xFF666666)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Incluir lote",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Si no está seleccionado, solo muestra artículos con lote por defecto",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Tercera fila: Card "Todos los artículos"
                    SeleccionarTodoCard(
                        isSelected = isSelectAllChecked,
                        onToggle = { isChecked ->
                            if (isChecked) {
                                // Marcar inmediatamente el estado interno
                                isSelectAllChecked = true
                                // Seleccionar solo los artículos VISIBLES (filtrados)
                                selectAllVisible()
                            } else {
                                // Desmarcar inmediatamente el estado interno
                                isSelectAllChecked = false
                                // Ejecutar la función de limpiar selección
                                onClearSelection()
                            }
                        },
                        label = "Todos los artículos",
                        description = "Seleccionar/deseleccionar todos"
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Lista de artículos
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredArticulos) { articulo ->
                        ArticuloLoteItem(
                            articulo = articulo,
                            isSelected = selectedArticulosLotes.contains(articulo),
                            onToggle = { onToggleArticulo(articulo) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onConfirm(selectedArticulosLotes) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF4CAF50)
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color(0xFF4CAF50), Color(0xFF4CAF50))
                            )
                        ),
                        enabled = selectedArticulosLotes.isNotEmpty()
                    ) {
                        Text("Confirmar (${selectedArticulosLotes.size})", fontSize = 12.sp)
                    }
                }
            }
        }
    }
    
    // Diálogo de confirmación para incluir stock cero
    if (showConfirmStockCeroDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmStockCeroDialog = false },
            title = {
                Text(
                    text = "Confirmar acción",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Text(
                    text = "¿Está seguro de incluir stock cero?",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        includeZeroStock = true
                        showConfirmStockCeroDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Text("Sí", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showConfirmStockCeroDialog = false },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF4CAF50)
                    )
                ) {
                    Text("No")
                }
            }
        )
    }
}

@Composable
private fun ArticuloLoteItem(
    articulo: ArticuloLote,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color(0xFF4CAF50) else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(6.dp)
            ),
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE8F5E8) else Color(0xFFFFFFFF)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox de selección más pequeño
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF4CAF50)
                ),
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Información del artículo compacta
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Descripción del artículo (línea principal)
                Text(
                    text = articulo.artDesc,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) Color(0xFF2E7D32) else Color(0xFF000000),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                // Lote y cantidad en una línea
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Lote: ${articulo.ardeLote}",
                        fontSize = 11.sp,
                        color = Color(0xFF666666)
                    )
                    Text(
                        text = "Cantidad: ${articulo.cantidad}",
                        fontSize = 11.sp,
                        color = Color(0xFF666666)
                    )
                }
                
                Spacer(modifier = Modifier.height(2.dp))
                
                // Grupo y subgrupo en una línea (más compacto)
                Text(
                    text = "${articulo.grupDesc} | ${articulo.sugrDesc}",
                    fontSize = 10.sp,
                    color = Color(0xFF888888)
                )
            }
        }
    }
}
