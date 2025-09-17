package com.gloria.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
 import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gloria.data.model.ConteoRequestResponse

/**
 * Componente para mostrar el resultado del envío de conteos
 */
@Composable
fun EnvioConteoResultCard(
    response: ConteoRequestResponse?,
    isLoading: Boolean,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isLoading -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                response?.success == true -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                errorMessage != null -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header con icono y título
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = when {
                        isLoading -> Icons.Default.Info
                        response?.success == true -> Icons.Default.CheckCircle
                        errorMessage != null -> Icons.Default.Info
                        else -> Icons.Default.Info
                    },
                    contentDescription = "Estado",
                    tint = when {
                        isLoading -> MaterialTheme.colorScheme.primary
                        response?.success == true -> MaterialTheme.colorScheme.tertiary
                        errorMessage != null -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.primary
                    }
                )
                Text(
                    text = when {
                        isLoading -> "Enviando Conteos..."
                        response?.success == true -> "Envío Exitoso"
                        errorMessage != null -> "Error en el Envío"
                        else -> "Estado del Envío"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Contenido según el estado
            when {
                isLoading -> {
                    Text(
                        text = "Procesando conteos de verificación...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                response?.success == true -> {
                    // Mostrar detalles del éxito
                    Text(
                        text = response.message ?: "Conteos enviados exitosamente",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Estadísticas del envío
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Estadísticas del Envío",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Registros Insertados:")
                                Text(
                                    text = "${response.registrosInsertados ?: 0}",
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Tiempo de Procesamiento:")
                                Text(
                                    text = "${response.tiempoMs ?: 0}ms",
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Método:")
                                Text(
                                    text = response.method ?: "N/A",
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            response.winvdNroInvList?.let { inventarios ->
                                if (inventarios.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Inventarios Procesados:",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = inventarios.joinToString(", "),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
                
                errorMessage != null -> {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                
                else -> {
                    Text(
                        text = "No hay información disponible",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
