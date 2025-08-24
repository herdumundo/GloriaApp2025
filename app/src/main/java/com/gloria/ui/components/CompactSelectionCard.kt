package com.gloria.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactSelectionCard(
    label: String,
    selectedText: String?,
    onClick: () -> Unit,
    enabled: Boolean = true,
    isCompleted: Boolean = false,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.95f,
        animationSpec = tween(durationMillis = 200),
        label = "scale"
    )
    
    val elevation by animateFloatAsState(
        targetValue = if (enabled) 4f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "elevation"
    )
    
            Card(
            modifier = modifier
                .fillMaxWidth()
                .scale(scale),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    isCompleted -> Color(0xFFE8F5E8) // Verde claro para completado
                    selectedText != null -> Color(0xFFE8F5E8) // Verde claro para seleccionado
                    else -> MaterialTheme.colorScheme.surface
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp),
            border = BorderStroke(
                width = if (selectedText != null) 2.dp else 1.dp,
                color = when {
                    isCompleted -> Color(0xFF4CAF50) // Verde para completado
                    selectedText != null -> Color(0xFF4CAF50) // Verde para seleccionado
                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                }
            )
        ) {
        Column {
            // Header de la tarjeta (siempre visible)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = enabled) { onClick() }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Contenido principal
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selectedText != null) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
                        )
                        
                        // Icono de check para opciones seleccionadas
                        if (selectedText != null) {
                       //     Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Seleccionado",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    
                    if (selectedText != null) {
                       // Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = selectedText,
                            fontSize = 11.sp,
                            color = Color(0xFF2E7D32),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                      //  Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Seleccione...",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
                
                // Solo botón de flecha para indicar que es clickeable
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Seleccionar",
                    tint = if (selectedText != null) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
            
        }
    }
}
