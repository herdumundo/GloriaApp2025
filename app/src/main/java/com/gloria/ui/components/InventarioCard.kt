package com.gloria.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gloria.data.model.InventarioCard

/**
 * Card de inventario para la pantalla de Registro de Inventario
 * Diseño basado en la imagen proporcionada
 */
@Composable
fun InventarioCard(
    inventario: InventarioCard,
    onMenuClick: () -> Unit = {},
    onCheckClick: () -> Unit = {},
    onCardClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Determinar el color del card según el estado
    val cardColor = when (inventario.estado) {
        "P" -> Color(0xFF2E7D32) // Verde oscuro para procesado
        "S" -> Color(0xFF2E7D32) // Verde oscuro para procesado
        else -> Color(0xFF8B0000) // Rojo oscuro para activo
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clickable { onCardClick() },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Columna izquierda: Icono de tipo de conteo
            Column(
                modifier = Modifier.padding(end = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icono según tipo de conteo
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (inventario.winveTipo == "S") Icons.Default.SupervisorAccount   else Icons.Default.Person,
                        contentDescription = if (inventario.winveTipo == "S") "Simultáneo" else "Individual",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            // Columna derecha: Detalles del inventario (más compacto)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Encabezado: Número y Fecha en la misma línea
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOMA #${inventario.winvd_nro_inv}",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = inventario.fecha_toma.ifEmpty { "N/A" },
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Primera fila: SUC a la izquierda y badge a la derecha
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // SUC a la izquierda
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SUC:",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.width(60.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = inventario.sucursal.ifEmpty { "N/A" },
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                    
                    // Badge a la derecha
                    Row(
                        modifier = Modifier
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (inventario.winveTipo == "S") Icons.Default.SupervisorAccount else Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (inventario.winveTipo == "S") "SIMULTÁNEO" else "INDIVIDUAL",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // Resto de detalles en filas compactas
                CompactDetailRow(label = "DEP:", value = inventario.deposito.ifEmpty { "N/A" })
                CompactDetailRow(label = "ÁREA:", value = inventario.area_desc.ifEmpty { "N/A" })
                CompactDetailRow(label = "FAMILIA:", value = inventario.desc_familia.ifEmpty { "N/A" })
                CompactDetailRow(label = "TOMA:", value = inventario.tipo_toma.ifEmpty { "N/A" })

                // Mensaje de estado si está procesado
                if (inventario.estado == "P" || inventario.estado == "S") {
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Información",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(6.dp))
                        
                        Text(
                            text = "Ya se contó - Puede continuar",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

/**
 * Fila de detalle compacta con label y valor
 */
@Composable
private fun CompactDetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(60.dp)
        )
        
        Spacer(modifier = Modifier.width(4.dp))
        
        Text(
            text = value,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
    }
}


