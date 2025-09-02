package com.gloria.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
        else -> Color(0xFF8B0000) // Rojo oscuro para activo
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onCardClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Columna izquierda: Iconos
            Column(
                modifier = Modifier.width(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icono de menú (hamburger)
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menú",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Icono de check
                IconButton(
                    onClick = onCheckClick,
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Verificado",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Columna derecha: Detalles del inventario
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Número de toma
                DetailRow(
                    label = "NRO. DE TOMA:",
                    value = inventario.winvd_nro_inv.toString(),
                    isBold = true
                )
                
                // Fecha de toma
                DetailRow(
                    label = "FECHA TOMA:",
                    value = inventario.fecha_toma.ifEmpty { "N/A" }
                )
                
                // Sucursal
                DetailRow(
                    label = "SUCURSAL:",
                    value = inventario.sucursal.ifEmpty { "N/A" }
                )
                
                // Depósito
                DetailRow(
                    label = "DEPOSITO:",
                    value = inventario.deposito.ifEmpty { "N/A" }
                )
                
                // Área
                DetailRow(
                    label = "AREA:",
                    value = inventario.area_desc.ifEmpty { "N/A" }
                )
                
                // Departamento
                DetailRow(
                    label = "DEPARTAMENTO:",
                    value = inventario.dpto_desc.ifEmpty { "N/A" }
                )
                
                // Sección
                DetailRow(
                    label = "SECCION:",
                    value = inventario.secc_desc.ifEmpty { "N/A" }
                )
                
                // Familia
                DetailRow(
                    label = "FAMILIA:",
                    value = inventario.desc_familia.ifEmpty { "N/A" }
                )
                
                // Grupo
                DetailRow(
                    label = "GRUPO:",
                    value = inventario.desc_grupo_parcial.ifEmpty { "N/A" }
                )
                
                // Tipo de toma
                DetailRow(
                    label = "TOMA:",
                    value = inventario.tipo_toma.ifEmpty { "N/A" }
                )
                
                // Consolidado
                DetailRow(
                    label = "CONSOLIDADO:",
                    value = inventario.winvd_consolidado.ifEmpty { "N/A" }
                )
                
                // Mensaje de estado si está procesado
                if (inventario.estado == "P") {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Información",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "Ya se contó - Se puede continuar con el conteo",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

/**
 * Fila de detalle con label y valor
 */
@Composable
private fun DetailRow(
    label: String,
    value: String,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(120.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = value,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Preview del card de inventario
 */
@Composable
fun InventarioCardPreview() {
    val inventarioEjemplo = InventarioCard(
        winvd_nro_inv = 861,
        fecha_toma = "22/08/2025 09:54",
        area_desc = "GENERAL",
        dpto_desc = "GENERAL",
        tipo_toma = "MANUAL",
        secc_desc = "GENERAL",
        winvd_consolidado = "N",
        desc_grupo_parcial = "TODOS",
        desc_familia = "CERVEZA",
        sucursal = "CASA CENTRAL",
        deposito = "D1",
        estado = "A"
    )
    
    InventarioCard(
        inventario = inventarioEjemplo,
        onMenuClick = { /* TODO */ },
        onCheckClick = { /* TODO */ }
    )
}
