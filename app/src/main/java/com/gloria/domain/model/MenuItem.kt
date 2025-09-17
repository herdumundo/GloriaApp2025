package com.gloria.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Modelo para los items del menú lateral
 */
data class MenuItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val route: String
)

/**
 * Lista de items del menú principal para la aplicación de inventario
 */
object MenuItems {
    val items = listOf(
        MenuItem(
            id = "registro_toma",
            title = "Registro de Toma",
            icon = Icons.Default.Edit,
            route = "registro_toma"
        ),
        MenuItem(
            id = "registro_inventario",
            title = "Registro de Inventario",
            icon = Icons.AutoMirrored.Filled.List,
            route = "registro_inventario"
        ),
        MenuItem(
            id = "cancelacion_inventario",
            title = "Cancelación de Inventario",
            icon = Icons.Default.Close,
            route = "cancelacion_inventario"
        ),
        MenuItem(
            id = "exportar_inventario",
            title = "Enviar datos",
            icon = Icons.AutoMirrored.Filled.Send,
            route = "exportar_inventario"
        ) ,
        MenuItem(
            id = "sincronizar_datos",
            title = "Sincronizar Datos",
            icon = Icons.Default.Refresh,
            route = "sincronizar_datos"
        ),
        MenuItem(
            id = "informe_conteos_pendientes",
            title = "Informe de Conteos Pendientes",
            icon = Icons.Default.ExitToApp,
            route = "informe_conteos_pendientes"
        )
    )
}
