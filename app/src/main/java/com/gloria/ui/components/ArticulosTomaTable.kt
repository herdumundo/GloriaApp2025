package com.gloria.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gloria.data.model.ArticuloToma
import androidx.compose.foundation.clickable

@Composable
fun ArticulosTomaTable(
    articulos: List<ArticuloToma>,
    onArticuloClick: (ArticuloToma) -> Unit,
    onSubgrupoClick: (String) -> Unit,
    onGrupoClick: (String) -> Unit,
    onFamiliaClick: (String) -> Unit,
    onSelectAllClick: () -> Unit
) {
    val horizontalScrollState = rememberScrollState()
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header de la tabla
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            // Columna fija (checkbox)
            Box(
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { onSelectAllClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (articulos.all { it.isSelected }) "✓" else "☐",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Columnas scrolleables
            Row(
                modifier = Modifier.horizontalScroll(horizontalScrollState)
            ) {
                // Secu
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(50.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Secu",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                // Código
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(50.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Código",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                // Descripción
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(50.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Descripción",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                // Lote
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(50.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Lote",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                // Vencimiento
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(50.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Vencimiento",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                // Familia
                Box(
                    modifier = Modifier
                        .width(150.dp)
                        .height(50.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Familia (Click)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                // Grupo
                Box(
                    modifier = Modifier
                        .width(150.dp)
                        .height(50.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Grupo (Click)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                // Subgrupo
                Box(
                    modifier = Modifier
                        .width(150.dp)
                        .height(50.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Subgrupo (Click)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
        
        // Filas de datos
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(
                items = articulos,
                key = { index, articulo -> "${articulo.winvdSecu}_${articulo.winvdArt}_${articulo.winvdLote}_$index" }
            ) { index, articulo ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (index % 2 == 0) MaterialTheme.colorScheme.surface
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                ) {
                    // Columna fija (checkbox)
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(60.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            .background(
                                if (index % 2 == 0) MaterialTheme.colorScheme.surface
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                            .clickable { onArticuloClick(articulo) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (articulo.isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Seleccionado",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Columnas scrolleables
                    Row(
                        modifier = Modifier.horizontalScroll(horizontalScrollState)
                    ) {
                        // Secu
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(60.dp)
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                .background(
                                    if (index % 2 == 0) MaterialTheme.colorScheme.surface
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = articulo.winvdSecu,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }
                        
                        // Código
                        Box(
                            modifier = Modifier
                                .width(100.dp)
                                .height(60.dp)
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                .background(
                                    if (index % 2 == 0) MaterialTheme.colorScheme.surface
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = articulo.winvdArt,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }
                        
                        // Descripción
                        Box(
                            modifier = Modifier
                                .width(200.dp)
                                .height(60.dp)
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                .background(
                                    if (index % 2 == 0) MaterialTheme.colorScheme.surface
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                )
                                .padding(4.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = articulo.artDesc,
                                fontSize = 11.sp,
                                maxLines = 2,
                                textAlign = TextAlign.Start
                            )
                        }
                        
                        // Lote
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(60.dp)
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                .background(
                                    if (index % 2 == 0) MaterialTheme.colorScheme.surface
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = articulo.winvdLote,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        
                        // Vencimiento
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(60.dp)
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                .background(
                                    if (index % 2 == 0) MaterialTheme.colorScheme.surface
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = articulo.ardeFecVtoLote,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        
                        // Familia
                        Box(
                            modifier = Modifier
                                .width(150.dp)
                                .height(60.dp)
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                .background(
                                    if (index % 2 == 0) MaterialTheme.colorScheme.surface
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                )
                                .padding(4.dp)
                                .clickable { onFamiliaClick(articulo.fliaDesc) },
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = articulo.fliaDesc,
                                fontSize = 11.sp,
                                maxLines = 1,
                                textAlign = TextAlign.Start,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        // Grupo
                        Box(
                            modifier = Modifier
                                .width(150.dp)
                                .height(60.dp)
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                .background(
                                    if (index % 2 == 0) MaterialTheme.colorScheme.surface
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                )
                                .padding(4.dp)
                                .clickable { onGrupoClick(articulo.grupDesc) },
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = articulo.grupDesc,
                                fontSize = 11.sp,
                                maxLines = 1,
                                textAlign = TextAlign.Start,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        // Subgrupo
                        Box(
                            modifier = Modifier
                                .width(150.dp)
                                .height(60.dp)
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                .background(
                                    if (index % 2 == 0) MaterialTheme.colorScheme.surface
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                )
                                .padding(4.dp)
                                .clickable { onSubgrupoClick(articulo.sugrDesc) },
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = articulo.sugrDesc,
                                fontSize = 11.sp,
                                maxLines = 1,
                                textAlign = TextAlign.Start,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}