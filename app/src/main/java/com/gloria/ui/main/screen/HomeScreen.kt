package com.gloria.ui.main.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

@Composable
fun HomeScreen(
    username: String,
    sucursal: String? = null,
    onLogoutClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val isSmallScreen = screenHeight < 600.dp
    
    // Padding adaptativo según el tamaño de pantalla
    val horizontalPadding = if (isSmallScreen) 12.dp else 24.dp
    val verticalPadding = if (isSmallScreen) 8.dp else 16.dp
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(if (isSmallScreen) 8.dp else 16.dp)
    ) {
        // Header con información del usuario
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(if (isSmallScreen) 12.dp else 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Usuario",
                    modifier = Modifier.size(if (isSmallScreen) 36.dp else 48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = if (isSmallScreen) 12.dp else 16.dp)
                ) {
                    Text(
                        text = "Bienvenido",
                        style = if (isSmallScreen) MaterialTheme.typography.titleMedium 
                               else MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = username,
                        style = if (isSmallScreen) MaterialTheme.typography.bodyLarge 
                               else MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (sucursal != null) {
                        Text(
                            text = "Sucursal: $sucursal",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Contenido principal
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(if (isSmallScreen) 16.dp else 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "¡Has iniciado sesión exitosamente!",
                    style = if (isSmallScreen) MaterialTheme.typography.titleLarge 
                           else MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = if (isSmallScreen) 8.dp else 16.dp)
                )
                
                Text(
                    text = "Esta es tu pantalla principal. Aquí puedes agregar el contenido de tu aplicación de inventario.",
                    style = if (isSmallScreen) MaterialTheme.typography.bodyMedium 
                           else MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = if (isSmallScreen) 16.dp else 32.dp)
                )
                
                Text(
                    text = "Usuario: $username",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = if (isSmallScreen) 16.dp else 24.dp)
                )
                
                // Contenido adicional para hacer el scroll más evidente
                Text(
                    text = "Funcionalidades disponibles:",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "• Gestión de inventario\n• Toma de inventario\n• Sincronización de datos\n• Reportes y estadísticas",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = if (isSmallScreen) 16.dp else 24.dp)
                )
                
                Text(
                    text = "¡Explora todas las opciones disponibles en el menú principal!",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = if (isSmallScreen) 8.dp else 16.dp)
                )
            }
        }

        // Botón de cerrar sesión
        Button(
            onClick = onLogoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isSmallScreen) 48.dp else 56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Cerrar sesión",
                modifier = Modifier.size(if (isSmallScreen) 18.dp else 20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Cerrar Sesión", 
                fontSize = if (isSmallScreen) 14.sp else 16.sp
            )
        }
        
        // Espacio adicional para asegurar scroll
        Spacer(modifier = Modifier.height(if (isSmallScreen) 24.dp else 32.dp))
        
        // Información adicional
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(if (isSmallScreen) 12.dp else 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Información del Sistema",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "Versión: 1.0.0\nÚltima actualización: ${java.time.LocalDate.now()}",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
