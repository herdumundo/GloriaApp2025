package com.gloria.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun ExitConfirmationDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit = {},
    navController: NavHostController? = null,
    route: String? = null,
    title: String = "Salir",
    message: String = "¿Estás seguro de que deseas salir?",
    warningMessage: String? = null,
    confirmButtonText: String = "Sí, salir",
    dismissButtonText: String = "Cancelar",
    iconColor: Color = Color(0xFF8B0000),
    confirmButtonColor: Color = Color(0xFF8B0000)
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Salir",
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        color = iconColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (warningMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = warningMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDismiss()
                        if (navController != null && route != null) {
                            navController.navigate(route)
                        } else {
                            onConfirm()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = confirmButtonColor
                    )
                ) {
                    Text(confirmButtonText)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text(dismissButtonText)
                }
            }
        )
    }
}
