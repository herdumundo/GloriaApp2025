package com.gloria.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gloria.data.model.ArticuloLote

@Composable
fun ArticulosSeleccionadosGrid(
    articulos: List<ArticuloLote>,
    onRemoveArticulo: (ArticuloLote) -> Unit
) {
    // ScrollState compartido para sincronizar cabecera y filas
    val horizontalScrollState = rememberScrollState()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Cabecera de la grilla
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Productos Seleccionados (${articulos.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                // Botón para limpiar todos (opcional)
                TextButton(
                    onClick = { 
                        articulos.forEach { onRemoveArticulo(it) }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Limpiar Todo", fontSize = 12.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Cabecera de columnas con scroll horizontal sincronizado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                    .padding(12.dp)
                    .horizontalScroll(horizontalScrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Columna 1: Descripción del Artículo
                Text(
                    text = "Artículo",
                    modifier = Modifier.width(180.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                // Columna 2: Lote
                Text(
                    text = "Lote",
                    modifier = Modifier.width(70.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                // Columna 3: Cantidad
                Text(
                    text = "Cantidad",
                    modifier = Modifier.width(70.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                // Columna 4: Familia
                Text(
                    text = "Familia",
                    modifier = Modifier.width(100.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                // Columna 5: Grupo
                Text(
                    text = "Grupo",
                    modifier = Modifier.width(100.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                // Columna 6: Subgrupo
                Text(
                    text = "Subgrupo",
                    modifier = Modifier.width(120.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Lista de artículos con scroll horizontal sincronizado
            LazyColumn(
                modifier = Modifier.heightIn(max = 300.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(
                    items = articulos,
                    key = { index, articulo -> "${articulo.artCodigo}_${articulo.ardeLote}_$index" }
                ) { index, articulo ->
                    ArticuloGridRow(
                        articulo = articulo,
                        onRemove = { onRemoveArticulo(articulo) },
                        horizontalScrollState = horizontalScrollState
                    )
                }
            }
        }
    }
}

@Composable
private fun ArticuloGridRow(
    articulo: ArticuloLote,
    onRemove: () -> Unit,
    horizontalScrollState: ScrollState
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .horizontalScroll(horizontalScrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Columna 1: Descripción del Artículo
            Text(
                text = articulo.artDesc,
                modifier = Modifier.width(180.dp),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )
            
            // Columna 2: Lote
            Text(
                text = articulo.ardeLote,
                modifier = Modifier.width(70.dp),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            // Columna 3: Cantidad
            Text(
                text = articulo.cantidad.toString(),
                modifier = Modifier.width(70.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
                )
            
            // Columna 4: Familia
            Text(
                text = articulo.fliaDesc,
                modifier = Modifier.width(100.dp),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            
            // Columna 5: Grupo
            Text(
                text = articulo.grupDesc,
                modifier = Modifier.width(100.dp),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            
            // Columna 6: Subgrupo
            Text(
                text = articulo.sugrDesc,
                modifier = Modifier.width(120.dp),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}
