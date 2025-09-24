package com.gloria.domain.usecase.permission

import com.gloria.domain.usecase.permission.SyncUserPermissionsUseCase
import javax.inject.Inject

/**
 * Ejemplo de cómo usar la tabla de permisos para el usuario INVAP
 * Basado en la consulta: SELECT DISTINCT formulario, nombre FROM v_web_operador_rol_prog WHERE login_o = 'INVAP'
 */
class ExampleUserPermissions @Inject constructor(
    private val syncUserPermissionsUseCase: SyncUserPermissionsUseCase
) {
    
    /**
     * Sincroniza los permisos del usuario INVAP basado en los datos de la imagen
     * Los formularios que tiene INVAP son: STKW001, STKW002, STKW003, STKW004
     */
    suspend fun syncInvapPermissions() {
        val invapFormularios = listOf(
            "STKW001" to "INVENTARIO",
            "STKW002" to "INVENTARIO", 
            "STKW003" to "INVENTARIO",
            "STKW004" to "INVENTARIO"
        )
        
        syncUserPermissionsUseCase("INVAP", invapFormularios)
    }
    
    /**
     * Ejemplo de cómo sincronizar permisos para otros usuarios
     */
    suspend fun syncUserPermissionsExample(username: String, formularios: List<String>) {
        // Opción 1: Solo con códigos de formulario
        syncUserPermissionsUseCase(username, formularios, "INVENTARIO")
        
        // Opción 2: Con formulario y nombre específico
        val formulariosConNombre = formularios.map { formulario ->
            formulario to "INVENTARIO"
        }
        syncUserPermissionsUseCase(username, formulariosConNombre)
    }
    
    /**
     * Ejemplo de permisos específicos por pantalla
     */
    suspend fun setupDefaultPermissions() {
        // Usuario con acceso completo
        val adminFormularios = listOf("STKW001", "STKW002", "STKW003", "STKW004", "STKW005")
        syncUserPermissionsUseCase("ADMIN", adminFormularios, "ADMINISTRADOR")
        
        // Usuario solo de lectura
        val readOnlyFormularios = listOf("STKW005") // Solo informe
        syncUserPermissionsUseCase("READONLY", readOnlyFormularios, "SOLO_LECTURA")
        
        // Usuario operativo básico
        val operativoFormularios = listOf("STKW001", "STKW002") // Solo registro y toma
        syncUserPermissionsUseCase("OPERATIVO", operativoFormularios, "OPERATIVO")
    }
}
