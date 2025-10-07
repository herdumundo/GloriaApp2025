package com.gloria.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gloria.data.model.ArticuloLote

@Composable
fun ArticulosEncontradosDialog(
    articulosLotes: List<ArticuloLote>,
    selectedArticulosLotes: List<ArticuloLote>,
    onArticuloToggle: (ArticuloLote) -> Unit,
    onSelectAll: (List<ArticuloLote>) -> Unit,
    onDeselectAll: () -> Unit,
    onUpdateInventarioVisible: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    inventarioVisible: Boolean = false,
    autoSelectAll: Boolean = false, // Nuevo parámetro para selección automática
    disableDeselection: Boolean = false, // Nuevo parámetro para deshabilitar deselección
    modifier: Modifier = Modifier
) {
    var searchText by remember { mutableStateOf("") }
    var showZeroStock by remember { mutableStateOf(false) }
    var consolidar by remember { mutableStateOf(false) }
    
    // Selección automática cuando autoSelectAll es true
    LaunchedEffect(autoSelectAll, articulosLotes) {
        if (autoSelectAll && articulosLotes.isNotEmpty() && selectedArticulosLotes.isEmpty()) {
            onSelectAll(articulosLotes)
        }
    }
    
    // Mantener selección automática cuando cambia el filtro de stock cero en toma por criterio
    LaunchedEffect(autoSelectAll, showZeroStock, articulosLotes, inventarioVisible) {
        if (autoSelectAll && articulosLotes.isNotEmpty()) {
            // Si es toma por criterio, siempre mantener todos los artículos seleccionados
            // Usar los artículos filtrados y consolidados para la selección
            val stockFiltered = if (showZeroStock) {
                articulosLotes
            } else {
                articulosLotes.filter { it.cantidad > 0 }
            }
            
            val searchFiltered = if (searchText.isEmpty()) {
                stockFiltered
            } else {
                stockFiltered.filter { articulo ->
                    articulo.artDesc.contains(searchText, ignoreCase = true) ||
                    articulo.artCodigo.contains(searchText, ignoreCase = true) ||
                    articulo.fliaDesc.contains(searchText, ignoreCase = true) ||
                    articulo.grupDesc.contains(searchText, ignoreCase = true) ||
                    articulo.sugrDesc.contains(searchText, ignoreCase = true) ||
                    articulo.cantidad.toString().contains(searchText) ||
                    articulo.ardeLote.contains(searchText, ignoreCase = true)
                }
            }
            
            val finalArticulos = if (consolidar) {
                searchFiltered.groupBy { it.artCodigo }
                    .map { (_, articulosDelMismoCodigo) ->
                        val primerArticulo = articulosDelMismoCodigo.first()
                        val cantidadTotal = articulosDelMismoCodigo.sumOf { it.cantidad }
                        
                        primerArticulo.copy(
                            cantidad = cantidadTotal,
                            ardeLote = "N/A",
                            concatID = "${primerArticulo.artCodigo}_CONSOLIDATED",
                            inventarioVisible = if (articulosDelMismoCodigo.any { it.inventarioVisible == "Y" }) "Y" else "N"
                        )
                    }
            } else {
                searchFiltered
            }
            
            onSelectAll(finalArticulos)
        }
    }
    
    // Mantener selección automática cuando cambia la consolidación en toma por criterio
    LaunchedEffect(autoSelectAll, consolidar, articulosLotes, inventarioVisible) {
        if (autoSelectAll && articulosLotes.isNotEmpty()) {
            // Si es toma por criterio, siempre mantener todos los artículos seleccionados
            // Usar los artículos filtrados y consolidados para la selección
            val stockFiltered = if (showZeroStock) {
                articulosLotes
            } else {
                articulosLotes.filter { it.cantidad > 0 }
            }
            
            val searchFiltered = if (searchText.isEmpty()) {
                stockFiltered
            } else {
                stockFiltered.filter { articulo ->
                    articulo.artDesc.contains(searchText, ignoreCase = true) ||
                    articulo.artCodigo.contains(searchText, ignoreCase = true) ||
                    articulo.fliaDesc.contains(searchText, ignoreCase = true) ||
                    articulo.grupDesc.contains(searchText, ignoreCase = true) ||
                    articulo.sugrDesc.contains(searchText, ignoreCase = true) ||
                    articulo.cantidad.toString().contains(searchText) ||
                    articulo.ardeLote.contains(searchText, ignoreCase = true)
                }
            }
            
            val finalArticulos = if (consolidar) {
                searchFiltered.groupBy { it.artCodigo }
                    .map { (_, articulosDelMismoCodigo) ->
                        val primerArticulo = articulosDelMismoCodigo.first()
                        val cantidadTotal = articulosDelMismoCodigo.sumOf { it.cantidad }
                        
                        primerArticulo.copy(
                            cantidad = cantidadTotal,
                            ardeLote = "N/A",
                            concatID = "${primerArticulo.artCodigo}_CONSOLIDATED",
                            inventarioVisible = if (articulosDelMismoCodigo.any { it.inventarioVisible == "Y" }) "Y" else "N"
                        )
                    }
            } else {
                searchFiltered
            }
            
            onSelectAll(finalArticulos)
        }
    }
    
    // Función modificada para manejar toggle con deselección deshabilitada
    val handleArticuloToggle = { articulo: ArticuloLote ->
        if (!disableDeselection || !selectedArticulosLotes.contains(articulo)) {
            onArticuloToggle(articulo)
        }
    }
    
    // Filtrar y consolidar artículos basado en la búsqueda, stock, inventario visible y consolidación
    val filteredArticulos = remember(articulosLotes, searchText, showZeroStock, consolidar, inventarioVisible) {
        val stockFiltered = if (showZeroStock) {
            articulosLotes
        } else {
            articulosLotes.filter { it.cantidad > 0 }
        }
        
        val searchFiltered = if (searchText.isEmpty()) {
            stockFiltered
        } else {
            stockFiltered.filter { articulo ->
                articulo.artDesc.contains(searchText, ignoreCase = true) ||
                articulo.artCodigo.contains(searchText, ignoreCase = true) ||
                articulo.fliaDesc.contains(searchText, ignoreCase = true) ||
                articulo.grupDesc.contains(searchText, ignoreCase = true) ||
                articulo.sugrDesc.contains(searchText, ignoreCase = true) ||
                articulo.cantidad.toString().contains(searchText) ||
                articulo.ardeLote.contains(searchText, ignoreCase = true)
            }
        }
        
        // Consolidar artículos si está activado
        if (consolidar) {
            searchFiltered.groupBy { it.artCodigo }
                .map { (_, articulosDelMismoCodigo) ->
                    val primerArticulo = articulosDelMismoCodigo.first()
                    val cantidadTotal = articulosDelMismoCodigo.sumOf { it.cantidad }
                    
                    // Crear un artículo consolidado
                    primerArticulo.copy(
                        cantidad = cantidadTotal,
                        ardeLote = "N/A", // Lote consolidado
                        concatID = "${primerArticulo.artCodigo}_CONSOLIDATED", // ID único para consolidado
                        inventarioVisible = if (articulosDelMismoCodigo.any { it.inventarioVisible == "Y" }) "Y" else "N"
                    )
                }
        } else {
            searchFiltered
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = { Text("Artículos Encontrados") },
        text = {
            Column {
                val totalWithStock = articulosLotes.count { it.cantidad > 0 }
                val totalZeroStock = articulosLotes.count { it.cantidad == 0.0 }

                // Contador de seleccionados
                Text(
                    text = "Seleccionados: ${selectedArticulosLotes.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Controles en columna
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val allSelected = filteredArticulos.isNotEmpty() && filteredArticulos.all { it in selectedArticulosLotes }

                    // Checkbox para marcar inventario simultaneo
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            showZeroStock = !showZeroStock
                            // Si es toma por criterio, mantener selección automática
                            if (autoSelectAll && articulosLotes.isNotEmpty()) {
                                onSelectAll(articulosLotes)
                            }
                        }
                    ) {
                        Checkbox(
                            checked = showZeroStock,
                            onCheckedChange = {
                                showZeroStock = it
                                // Si es toma por criterio, mantener selección automática
                                if (autoSelectAll && articulosLotes.isNotEmpty()) {
                                    onSelectAll(articulosLotes)
                                }
                            },
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Permitir toma para conteo en simultaneo",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    // Checkbox para stock cero
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            showZeroStock = !showZeroStock
                            // Si es toma por criterio, mantener selección automática
                            if (autoSelectAll && articulosLotes.isNotEmpty()) {
                                onSelectAll(articulosLotes)
                            }
                        }
                    ) {
                        Checkbox(
                            checked = showZeroStock,
                            onCheckedChange = {
                                showZeroStock = it
                                // Si es toma por criterio, mantener selección automática
                                if (autoSelectAll && articulosLotes.isNotEmpty()) {
                                    onSelectAll(articulosLotes)
                                }
                            },
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mostrar artículos con stock cero",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    // Checkbox para consolidar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { 
                            consolidar = !consolidar
                            // Si es toma por criterio, mantener selección automática
                            if (autoSelectAll && articulosLotes.isNotEmpty()) {
                                onSelectAll(articulosLotes)
                            }
                        }
                    ) {
                        Checkbox(
                            checked = consolidar,
                            onCheckedChange = { 
                                consolidar = it
                                // Si es toma por criterio, mantener selección automática
                                if (autoSelectAll && articulosLotes.isNotEmpty()) {
                                    onSelectAll(articulosLotes)
                                }
                            },
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Consolidar artículos por código",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    // Checkbox para inventario con stock visible
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { 
                            onUpdateInventarioVisible(!inventarioVisible)
                            // Si es toma por criterio, mantener selección automática
                            if (autoSelectAll && articulosLotes.isNotEmpty()) {
                                onSelectAll(articulosLotes)
                            }
                        }
                    ) {
                        Checkbox(
                            checked = inventarioVisible,
                            onCheckedChange = { 
                                onUpdateInventarioVisible(it)
                                // Si es toma por criterio, mantener selección automática
                                if (autoSelectAll && articulosLotes.isNotEmpty()) {
                                    onSelectAll(articulosLotes)
                                }
                            },
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Marcar seleccionados como inventario visible",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    // Checkbox para seleccionar todos
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { 
                            if (!disableDeselection) {
                                if (allSelected) {
                                    onDeselectAll()
                                } else {
                                    onSelectAll(filteredArticulos)
                                }
                            }
                        }
                    ) {
                        Checkbox(
                            checked = allSelected,
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    onSelectAll(filteredArticulos)
                                } else if (!disableDeselection) {
                                    onDeselectAll()
                                }
                            },
                            enabled = !disableDeselection || !allSelected, // Deshabilitar si está deshabilitada la deselección y ya está seleccionado
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (disableDeselection && allSelected) {
                                "Todos los artículos seleccionados (por criterio)"
                            } else if (consolidar) {
                                "Seleccionar todos los artículos consolidados"
                            } else {
                                "Seleccionar todos los artículos"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (disableDeselection && allSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Buscador compacto
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Buscar...", style = MaterialTheme.typography.bodySmall) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(
                                onClick = { searchText = "" },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Limpiar",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp)
                ) {
                    if (filteredArticulos.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No se encontraron artículos",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        items(filteredArticulos) { articulo ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .clickable { 
                                        if (!disableDeselection || !selectedArticulosLotes.contains(articulo)) {
                                            handleArticuloToggle(articulo)
                                        }
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedArticulosLotes.contains(articulo))
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                                    else
                                        MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = if (selectedArticulosLotes.contains(articulo)) 0.dp else 1.dp
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = selectedArticulosLotes.contains(articulo),
                                        onCheckedChange = { 
                                            if (!disableDeselection || !selectedArticulosLotes.contains(articulo)) {
                                                handleArticuloToggle(articulo)
                                            }
                                        },
                                        enabled = !disableDeselection || !selectedArticulosLotes.contains(articulo)
                                    )
                                    
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    Column(modifier = Modifier.weight(1f)) {
                                        // Primera fila: Código y Stock
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Código: ${articulo.artCodigo}",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            
                                            Text(
                                                text = "Stock: ${articulo.cantidad.toInt()}",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = if (articulo.cantidad > 0) 
                                                    MaterialTheme.colorScheme.tertiary 
                                                else 
                                                    MaterialTheme.colorScheme.error
                                            )
                                        }
                                        
                                        Spacer(modifier = Modifier.height(2.dp))
                                        
                                        // Segunda fila: Lote
                                        Text(
                                            text = "Lote: ${articulo.ardeLote}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (articulo.ardeLote == "N/A") 
                                                MaterialTheme.colorScheme.onSurfaceVariant 
                                            else 
                                                MaterialTheme.colorScheme.onSurface,
                                            fontWeight = if (articulo.ardeLote == "N/A") FontWeight.Normal else FontWeight.Medium
                                        )
                                        
                                        Spacer(modifier = Modifier.height(2.dp))
                                        
                                        // Descripción del artículo (más pequeña)
                                        Text(
                                            text = articulo.artDesc,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        
                                        Spacer(modifier = Modifier.height(4.dp))
                                        
                                        // Familia y Grupo en la misma fila
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Text(
                                                text = "Familia: ${articulo.fliaDesc}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                text = "Grupo: ${articulo.grupDesc}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                        
                                        Spacer(modifier = Modifier.height(2.dp))
                                        
                                        // Subgrupo e Inventario Visible
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Subgrupo: ${articulo.sugrDesc}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.tertiary,
                                                modifier = Modifier.weight(1f)
                                            )
                                            
                                            // Mostrar el estado dinámico basado en selección y checkbox
                                            val isVisible = if (selectedArticulosLotes.contains(articulo)) {
                                                inventarioVisible
                                            } else {
                                                articulo.inventarioVisible == "Y"
                                            }
                                            
                                            Text(
                                                text = if (isVisible) "✓ Visible" else "✗ No visible",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (isVisible) 
                                                    MaterialTheme.colorScheme.primary 
                                                else 
                                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
