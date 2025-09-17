package com.gloria.ui.exportaciones.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.gloria.ui.exportaciones.viewmodel.ExportacionesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportacionesScreen(
    navController: NavHostController,
    viewModel: ExportacionesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var mostrarConfirmacionRealizados by remember { mutableStateOf(false) }
    var mostrarConfirmacionVerificacion by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        // Título
        Text(
            text = "Exportaciones",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Selecciona el tipo de conteo a exportar",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Botón para enviar conteo realizado
        Button(
            onClick = { 
                mostrarConfirmacionRealizados = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Enviar Conteo Realizado",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Botón para enviar conteo para verificación
        Button(
            onClick = { 
                mostrarConfirmacionVerificacion = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Enviar Conteo para Verificación",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Información adicional
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Información",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "• Conteo Realizado: Envía inventarios completados y listos para procesamiento",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "• Conteo para Verificación: Envía inventarios que requieren revisión adicional antes de enviar el conteo realizado",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
    
    // Diálogo de confirmación para conteos realizados
    if (mostrarConfirmacionRealizados) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacionRealizados = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirmar Exportación")
                }
            },
            text = {
                Column {
                    Text(
                        text = "¿Está seguro que desea enviar los datos del conteo realizado?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Esta acción:",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "• Marcará los inventarios como cerrados",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "• Enviará los datos al servidor",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "• No se podrá deshacer",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarConfirmacionRealizados = false
                        viewModel.exportarConteosRealizados()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarConfirmacionRealizados = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Diálogo de confirmación para conteos de verificación
    if (mostrarConfirmacionVerificacion) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacionVerificacion = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirmar Envío para Verificación")
                }
            },
            text = {
                Column {
                    Text(
                        text = "¿Está seguro que desea enviar los datos del conteo para verificación?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Esta acción:",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "• Enviará los datos al servidor para revisión",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "• NO cambiará el estado de los inventarios",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "• Los inventarios permanecerán disponibles para edición",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarConfirmacionVerificacion = false
                        viewModel.exportarConteosParaVerificacion()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarConfirmacionVerificacion = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Diálogo de progreso
    if (uiState.isExportando) {
        AlertDialog(
            onDismissRequest = { /* No se puede cerrar */ },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Procesando...")
                }
            },
            text = {
                Text(uiState.mensajeProgreso)
            },
            confirmButton = {
                TextButton(
                    onClick = { /* No se puede cancelar */ },
                    enabled = false
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Diálogo de resultado
    uiState.mensajeResultado?.let { mensaje ->
        AlertDialog(
            onDismissRequest = { viewModel.limpiarMensajeResultado() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (uiState.exportacionExitosa) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (uiState.exportacionExitosa) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (uiState.exportacionExitosa) "Operación Exitosa" else "Error",
                        color = if (uiState.exportacionExitosa) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                }
            },
            text = { Text(mensaje) },
            confirmButton = {
                Button(
                    onClick = { viewModel.limpiarMensajeResultado() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.exportacionExitosa) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
}

