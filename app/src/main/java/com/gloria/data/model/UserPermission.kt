package com.gloria.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad para almacenar los permisos de pantalla de los usuarios
 */
@Entity(tableName = "user_permissions")
data class UserPermission(
    @PrimaryKey
    val id: String, // Formato: "username_formulario" (ej: "INVAP_STKW001")
    val username: String, // Usuario al que pertenece el permiso
    val formulario: String, // Código del formulario (STKW001, STKW002, etc.)
    val nombre: String, // Nombre descriptivo del permiso
    val granted: Boolean = true // Si el permiso está concedido
)

/**
 * Objeto para mapear códigos de pantalla a códigos de formulario
 */
object PermissionMapper {
    val screenToFormulario = mapOf(
        "registro_toma" to "STKW001",
        "registro_inventario" to "STKW002", 
        "cancelacion_inventario" to "STKW004",
        "exportar_inventario" to "STKW002",
        "sincronizar_datos" to "STKW001", // o STKW002 según necesidad
        "informe_conteos_pendientes" to "STKW005",
        "confirmacion_conteo_simultaneo" to "STKW005"
    )
    
    val formularioToScreen = mapOf(
        "STKW001" to listOf("registro_toma", "sincronizar_datos"),
        "STKW002" to listOf("registro_inventario", "exportar_inventario"),
        "STKW004" to listOf("cancelacion_inventario"),
        "STKW005" to listOf("informe_conteos_pendientes","confirmacion_conteo_simultaneo")
    )
}
