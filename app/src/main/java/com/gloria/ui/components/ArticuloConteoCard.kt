package com.gloria.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
import com.gloria.data.model.ArticuloInventario
import com.gloria.ui.inventario.viewmodel.EstadoConteo

/**
 * Card para mostrar un artículo del inventario con campos de entrada para conteo
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticuloConteoCard(
    articulo: ArticuloInventario,
    estadoConteo: EstadoConteo,
    onCajasChanged: (Int) -> Unit,
    onUnidadesChanged: (Int) -> Unit,
    onArticuloContado: (Int, Int) -> Unit = { _, _ -> }, // (articuloId, cantidadTotal)
    onEstadoConteoChanged: (EstadoConteo) -> Unit,
    modifier: Modifier = Modifier
) {
    // Usar el estado del ViewModel en lugar de remember local
    var cajasInput by remember { mutableStateOf(estadoConteo.cajasInput) }
    var unidadesInput by remember { mutableStateOf(estadoConteo.unidadesInput) }
    var totalAcumulado by remember { mutableStateOf(estadoConteo.totalAcumulado) }
    var ultimoValorIngresado by remember { mutableStateOf(estadoConteo.totalAcumulado) }
    var haSidoContado by remember { mutableStateOf(estadoConteo.haSidoContado) }
    
    // Sincronizar con el estado del ViewModel cuando cambie
    LaunchedEffect(estadoConteo) {
        cajasInput = estadoConteo.cajasInput
        unidadesInput = estadoConteo.unidadesInput
        totalAcumulado = estadoConteo.totalAcumulado
        ultimoValorIngresado = estadoConteo.totalAcumulado
        haSidoContado = estadoConteo.haSidoContado
    }
    
    // Calcular total automáticamente
    val totalCalculado = remember(cajasInput, unidadesInput, articulo.caja) {
        val cajas = cajasInput.toIntOrNull() ?: 0
        val unidades = unidadesInput.toIntOrNull() ?: 0
        (cajas * articulo.caja) + unidades
    }
    
    // Total final (acumulado + actual)
    val totalFinal = totalAcumulado + totalCalculado
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Información principal del artículo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Código y descripción del artículo
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${articulo.winvdArt} ${articulo.artDesc}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Text(
                        text = articulo.codBarra,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Información de clasificación en una sola fila
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Familia
                    Column {
                        Text(
                            text = "Familia",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = articulo.fliaDesc,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    // Grupo
                    Column {
                        Text(
                            text = "Grupo",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = articulo.grupDesc,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    // Lote
                    Column {
                        Text(
                            text = "Lote",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = articulo.winvdLote,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    // Vencimiento
                    Column {
                        Text(
                            text = "Vto",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = articulo.winvdFecVto,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Campos de entrada para conteo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Campo de cajas
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Cajas",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(2.dp))
                    
                    OutlinedTextField(
                        value = cajasInput,
                        onValueChange = { newValue -> 
                            // Si el campo estaba vacío o tenía "0" y el usuario empieza a escribir, limpiar el "0"
                            val processedValue = if ((cajasInput == "0" || cajasInput.isEmpty()) && newValue.startsWith("0") && newValue.length > 1) {
                                newValue.substring(1)
                            } else {
                                newValue
                            }
                            
                            cajasInput = processedValue
                            onCajasChanged(processedValue.toIntOrNull() ?: 0)
                            
                            // Actualizar estado en el ViewModel
                            val nuevoEstado = EstadoConteo(
                                totalAcumulado = totalAcumulado,
                                cajasInput = processedValue,
                                unidadesInput = unidadesInput,
                                haSidoContado = haSidoContado
                            )
                            onEstadoConteoChanged(nuevoEstado)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(6.dp)
                    )
                    
                    Text(
                        text = "Último ingresado: $ultimoValorIngresado",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Campo de unidades
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Unidades",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(2.dp))
                    
                    OutlinedTextField(
                        value = unidadesInput,
                        onValueChange = { newValue -> 
                            // Si el campo estaba vacío o tenía "0" y el usuario empieza a escribir, limpiar el "0"
                            val processedValue = if ((unidadesInput == "0" || unidadesInput.isEmpty()) && newValue.startsWith("0") && newValue.length > 1) {
                                newValue.substring(1)
                            } else {
                                newValue
                            }
                            
                            unidadesInput = processedValue
                            onUnidadesChanged(processedValue.toIntOrNull() ?: 0)
                            
                            // Actualizar estado en el ViewModel
                            val nuevoEstado = EstadoConteo(
                                totalAcumulado = totalAcumulado,
                                cajasInput = cajasInput,
                                unidadesInput = processedValue,
                                haSidoContado = haSidoContado
                            )
                            onEstadoConteoChanged(nuevoEstado)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(6.dp)
                    )
                    
                    Text(
                        text = "Último ingresado: $ultimoValorIngresado",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Total y botón Contar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Total o estado
                Text(
                    text = if (haSidoContado) "Total: $totalFinal" else "Total: ${articulo.winvdCantInv}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (haSidoContado) Color(0xFF2E7D32) else Color(0xFF2E7D32)
                )
                
                // Botón Contar
                Button(
                    onClick = {
                        // Agregar al total acumulado (ya incluye la cantidad base si existe)
                        totalAcumulado += totalCalculado
                        // Guardar como último valor ingresado
                        ultimoValorIngresado = totalCalculado
                        // Marcar como contado
                        haSidoContado = true
                        
                        // Log para debug
                        Log.d("LogConteo", "=== ENVIANDO CONTEO DESDE CARD ===")
                        Log.d("LogConteo", "Artículo ID: ${articulo.winvdSecu}")
                        Log.d("LogConteo", "Total acumulado enviado: $totalAcumulado")
                        Log.d("LogConteo", "Total calculado: $totalCalculado")
                        Log.d("LogConteo", "Cantidad base (winvdCantInv): ${articulo.winvdCantInv}")
                        
                        // Actualizar estado en el ViewModel
                        val nuevoEstado = EstadoConteo(
                            totalAcumulado = totalAcumulado,
                            cajasInput = "0",
                            unidadesInput = "0",
                            haSidoContado = true
                        )
                        onEstadoConteoChanged(nuevoEstado)
                        
                        // Notificar al ViewModel que este artículo fue contado con la cantidad total final
                        onArticuloContado(articulo.winvdSecu, totalAcumulado)
                        
                        // Limpiar campos de entrada
                        cajasInput = "0"
                        unidadesInput = "0"
                        // Notificar cambios
                        onCajasChanged(0)
                        onUnidadesChanged(0)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (haSidoContado)   Color(0xFF2E7D32) else Color(0xFF830000)
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (haSidoContado) "CONTAR MÁS" else "CONTAR",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

// Preview comentado temporalmente para evitar errores de compilación
/*
@Preview(showBackground = true)
@Composable
fun ArticuloConteoCardPreview() {
    val articuloEjemplo = ArticuloInventario(
        winvd_nro_inv = 861,
        artDesc = "PERONI BOT. 660 CC UNID",
        winvdLote = "000000",
        winvdArt = "100830",
        winvdFecVto = "31/12/5000",
        winvdArea = 1,
        winvdDpto = 1,
        winvdSecc = 1,
        winvdFlia = 1,
        winvdGrupo = 1,
        winvdCantAct = 100,
        winvdCantInv = 0,
        winvdSecu = 1,
        grupDesc = "PERONI",
        fliaDesc = "CERVEZA",
        tomaRegistro = "A",
        codBarra = "8008440212412",
        caja = 24,
        gruesa = 144
    )
    
    ArticuloConteoCard(
        articulo = articuloEjemplo,
        onCajasChanged = { },
        onUnidadesChanged = { }
    )
}
*/
