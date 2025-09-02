package com.gloria.ui.reportes.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ReportesScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        
        Text(
            text = "Reportes y Análisis",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Genera reportes detallados:\n• Reportes de ventas\n• Análisis de inventario\n• Estadísticas de productos\n• Reportes personalizados",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { /* TODO: Implementar navegación a reportes */ },
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(48.dp)
        ) {
            Text("Próximamente...")
        }
    }
}

