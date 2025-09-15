package com.gloria.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun ConfirmationDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit = {},
    navController: NavHostController? = null,
    route: String? = null,
    
    // Estados del diálogo
    isLoading: Boolean = false,
    loadingProgress: Float = 0f ,
    successMessage: String? = null,
     // Contenido del diálogo
    title: String = "Confirmar",
    loadingTitle: String = "Procesando...",
    successTitle: String = "¡Éxito!",
    
    // Mensajes
    message: String = "¿Está seguro de que desea continuar?",
    loadingMessage: String = "Procesando...",
    successMainMessage: String = "Operación realizada correctamente",
    
    // Botones
    confirmButtonText: String = "Confirmar",
    successButtonText: String = "Aceptar",
    dismissButtonText: String = "Cancelar",
    
    // Colores
    confirmIconColor: Color = MaterialTheme.colorScheme.error,
    successIconColor: Color = MaterialTheme.colorScheme.primary,
    loadingColor: Color = MaterialTheme.colorScheme.error,
    confirmButtonColor: Color = MaterialTheme.colorScheme.error,
    
    // Iconos
    confirmIcon: ImageVector = Icons.Default.Close,
    successIcon: ImageVector = Icons.Default.Check
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { 
                if (!isLoading && successMessage == null) {
                    onDismiss()
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Título
                    Text(
                        text = when {
                            isLoading -> loadingTitle
                            successMessage != null -> successTitle
                            else -> title
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    // Contenido según el estado
                    when {
                        isLoading -> {
                            // Mostrar loading con porcentaje
                            CircularProgressIndicator(
                                progress = loadingProgress / 100f,
                                modifier = Modifier.size(80.dp),
                                strokeWidth = 8.dp,
                                color = loadingColor
                            )
                            
                            Text(
                                text = "Procesando... ${loadingProgress}%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            Text(
                                text = loadingMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        successMessage != null -> {
                            // Mostrar mensaje de éxito
                            Icon(
                                imageVector = successIcon,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = successIconColor
                            )
                            
                            Text(
                                text = successMainMessage,
                                style = MaterialTheme.typography.titleLarge,
                                color = successIconColor,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            
                            Text(
                                text = successMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                        }
                        else -> {
                            // Mostrar mensaje de confirmación
                            Icon(
                                imageVector = confirmIcon,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = confirmIconColor
                            )
                            
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            confirmButton = {
                when {
                    isLoading -> {
                        // No mostrar botón durante loading
                    }
                    successMessage != null -> {
                        TextButton(onClick = { 
                            onDismiss()
                            if (navController != null && route != null) {
                                navController.navigate(route)
                            } else {
                                onConfirm()
                            }
                        }) {
                            Text(successButtonText)
                        }
                    }
                    else -> {
                        TextButton(onClick = onConfirm) {
                            Text(confirmButtonText)
                        }
                    }
                }
            },
            dismissButton = {
                if (!isLoading && successMessage == null) {
                    TextButton(onClick = onDismiss) {
                        Text(dismissButtonText)
                    }
                }
            }
        )
    }
}
