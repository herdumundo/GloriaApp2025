package com.gloria.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.window.DialogProperties
import com.gloria.util.InventoryLogger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchableMultiSelectDialog(
    title: String,
    items: List<T>,
    selectedItems: List<T>,
    onItemToggled: (T) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    getItemText: (T) -> String,
    getItemSubtext: (T) -> String = { "" },
    showDialog: Boolean,
    showTodosOption: Boolean = false,
    isTodosSelected: Boolean = false,
    onTodosToggled: (() -> Unit)? = null
) {
    // Logging para debugging
    InventoryLogger.logInfo("SEARCHABLE_MULTI_DIALOG", "=== SEARCHABLE MULTI DIALOG DEBUG ===")
    InventoryLogger.logInfo("SEARCHABLE_MULTI_DIALOG", "Título: $title")
    InventoryLogger.logInfo("SEARCHABLE_MULTI_DIALOG", "showDialog: $showDialog")
    InventoryLogger.logInfo("SEARCHABLE_MULTI_DIALOG", "items.size: ${items.size}")
    InventoryLogger.logInfo("SEARCHABLE_MULTI_DIALOG", "selectedItems.size: ${selectedItems.size}")
    InventoryLogger.logInfo("SEARCHABLE_MULTI_DIALOG", "showTodosOption: $showTodosOption")
    InventoryLogger.logInfo("SEARCHABLE_MULTI_DIALOG", "isTodosSelected: $isTodosSelected")
    
    if (items.isNotEmpty()) {
        InventoryLogger.logInfo("SEARCHABLE_MULTI_DIALOG", "Primeros 3 items:")
        items.take(3).forEach { item ->
            InventoryLogger.logInfo("SEARCHABLE_MULTI_DIALOG", "  - ${getItemText(item)}")
        }
    } else {
        InventoryLogger.logWarning("SEARCHABLE_MULTI_DIALOG", "¡LISTA DE ITEMS VACÍA!")
    }
    InventoryLogger.logInfo("SEARCHABLE_MULTI_DIALOG", "=== FIN SEARCHABLE MULTI DIALOG DEBUG ===")

    var searchQuery by remember { mutableStateOf("") }
    
    val filteredItems = remember(items, searchQuery) {
        if (searchQuery.isBlank()) {
            items
        } else {
            items.filter { 
                getItemText(it).contains(searchQuery, ignoreCase = true) ||
                getItemSubtext(it).contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    if (showDialog) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.8f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Header con título y botón cerrar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Contador de seleccionados
                    Text(
                        text = "${selectedItems.size} de ${items.size} seleccionados",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Campo de búsqueda
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Buscar...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar"
                            )
                        },
                        trailingIcon = if (searchQuery.isNotEmpty()) {
                            {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Limpiar"
                                    )
                                }
                            }
                        } else null,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Lista de elementos
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp) // Reducido de 4.dp a 2.dp
                    ) {
                        // Opción "Todos" si está habilitada
                        if (showTodosOption && onTodosToggled != null) {
                            item {
                                TodosCheckboxRow(
                                    isSelected = isTodosSelected,
                                    onClick = onTodosToggled
                                )
                                Spacer(modifier = Modifier.height(4.dp)) // Reducido de 8.dp a 4.dp
                            }
                        }
                        
                        items(filteredItems) { item ->
                            MultiSelectItemRow(
                                item = item,
                                isSelected = selectedItems.contains(item),
                                getItemText = getItemText,
                                getItemSubtext = getItemSubtext,
                                onClick = { onItemToggled(item) }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Botones de acción
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar")
                        }
                        
                        Button(
                            onClick = onConfirm,
                            modifier = Modifier.weight(1f),
                            enabled = selectedItems.isNotEmpty()
                        ) {
                            Text("Confirmar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TodosCheckboxRow(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
            else 
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp), // Reducido de 12.dp a 8.dp
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onClick() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.tertiary
                )
            )
            
            Spacer(modifier = Modifier.width(8.dp)) // Reducido de 12.dp a 8.dp
            
            Column {
                Text(
                    text = "Todos los grupos",
                    fontSize = 15.sp, // Reducido de 16.sp a 15.sp
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Seleccionar/deseleccionar todos",
                    fontSize = 13.sp, // Reducido de 14.sp a 13.sp
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun <T> MultiSelectItemRow(
    item: T,
    isSelected: Boolean,
    getItemText: (T) -> String,
    getItemSubtext: (T) -> String,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    } else {
        Color.Transparent
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = if (isSelected) CardDefaults.cardElevation(defaultElevation = 2.dp) 
                   else CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp), // Reducido de 12.dp a 8.dp
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onClick() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary
                )
            )
            
            Spacer(modifier = Modifier.width(8.dp)) // Reducido de 12.dp a 8.dp
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = getItemText(item),
                    fontSize = 15.sp, // Reducido de 16.sp a 15.sp
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                
                val subtext = getItemSubtext(item)
                if (subtext.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp)) // Reducido de 4.dp a 2.dp
                    Text(
                        text = subtext,
                        fontSize = 13.sp, // Reducido de 14.sp a 13.sp
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
