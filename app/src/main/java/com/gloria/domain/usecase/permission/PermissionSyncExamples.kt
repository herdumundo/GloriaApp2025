package com.gloria.domain.usecase.permission

import android.util.Log
import javax.inject.Inject

/**
 * Ejemplos de cuándo y cómo sincronizar permisos desde Oracle a Room
 */
class PermissionSyncExamples @Inject constructor(
    private val syncUserPermissionsFromOracleUseCase: SyncUserPermissionsFromOracleUseCase,
    private val checkUserPermissionUseCase: CheckUserPermissionUseCase
) {
    
    /**
     * EJEMPLO 1: Sincronización durante el LOGIN
     * Este es el momento más común y recomendado
     */
    suspend fun syncOnLogin(username: String, password: String) {
        Log.d("PermissionSync", "=== SINCRONIZACIÓN EN LOGIN ===")
        
        // Sincronizar permisos del usuario al hacer login
        val result = syncUserPermissionsFromOracleUseCase(username, password)
        
        result.fold(
            onSuccess = {
                Log.d("PermissionSync", "Permisos sincronizados exitosamente en login para $username")
            },
            onFailure = { error ->
                Log.e("PermissionSync", "Error al sincronizar permisos en login para $username", error)
            }
        )
    }
    
    /**
     * EJEMPLO 2: Sincronización manual desde un botón o menú
     * Útil para actualizar permisos sin hacer logout/login
     */
    suspend fun syncManually(username: String, password: String) {
        Log.d("PermissionSync", "=== SINCRONIZACIÓN MANUAL ===")
        
        // El usuario puede forzar una sincronización manual
        val result = syncUserPermissionsFromOracleUseCase(username, password)
        
        result.fold(
            onSuccess = {
                Log.d("PermissionSync", "Permisos actualizados manualmente para $username")
            },
            onFailure = { error ->
                Log.e("PermissionSync", "Error en sincronización manual para $username", error)
            }
        )
    }
    
    /**
     * EJEMPLO 3: Verificación con sincronización automática
     * Cuando se verifica un permiso y no está en Room, sincroniza automáticamente
     */
    suspend fun checkPermissionWithAutoSync(username: String, password: String, screenId: String): Boolean {
        Log.d("PermissionSync", "=== VERIFICACIÓN CON AUTO-SYNC ===")
        
        // Primero verificar en Room (rápido)
        val hasPermission = checkUserPermissionUseCase(username, screenId)
        
        if (!hasPermission) {
            Log.d("PermissionSync", "Permiso no encontrado en Room, sincronizando desde Oracle...")
            
            // Si no tiene el permiso en Room, sincronizar desde Oracle
            val syncResult = syncUserPermissionsFromOracleUseCase(username, password)
            
            syncResult.fold(
                onSuccess = {
                    Log.d("PermissionSync", "Sincronización automática exitosa, verificando nuevamente...")
                    // Verificar nuevamente después de sincronizar
                    return checkUserPermissionUseCase(username, screenId)
                },
                onFailure = { error ->
                    Log.e("PermissionSync", "Error en sincronización automática", error)
                    return false
                }
            )
        }
        
        return hasPermission
    }
    
    /**
     * EJEMPLO 4: Sincronización masiva de todos los usuarios
     * Útil para administradores o sincronización inicial
     */
    suspend fun syncAllUsers() {
        Log.d("PermissionSync", "=== SINCRONIZACIÓN MASIVA ===")
        
      /*  val result = syncUserPermissionsFromOracleUseCase.syncAllUsersPermissions()
        
        result.fold(
            onSuccess = {
                Log.d("PermissionSync", "Sincronización masiva completada exitosamente")
            },
            onFailure = { error ->
                Log.e("PermissionSync", "Error en sincronización masiva", error)
            }
        )*/
    }
    
    /**
     * EJEMPLO 5: Sincronización periódica (ejemplo con Timer o WorkManager)
     * Para mantener los permisos actualizados automáticamente
     */
    suspend fun periodicSync(username: String, password: String) {
        Log.d("PermissionSync", "=== SINCRONIZACIÓN PERIÓDICA ===")
        
        // Esto se podría llamar cada X minutos o cuando la app vuelve del background
        val result = syncUserPermissionsFromOracleUseCase(username, password)
        
        result.fold(
            onSuccess = {
                Log.d("PermissionSync", "Sincronización periódica exitosa para $username")
            },
            onFailure = { error ->
                Log.e("PermissionSync", "Error en sincronización periódica para $username", error)
            }
        )
    }
    
    /**
     * EJEMPLO 6: Sincronización específica para el usuario INVAP
     */
    suspend fun syncInvapPermissions(password: String) {
        Log.d("PermissionSync", "=== SINCRONIZACIÓN ESPECÍFICA INVAP ===")
        
        // Sincronizar específicamente los permisos del usuario INVAP
        val result = syncUserPermissionsFromOracleUseCase("INVAP", password)
        
        result.fold(
            onSuccess = {
                Log.d("PermissionSync", "Permisos de INVAP sincronizados: STKW001, STKW002, STKW003, STKW004")
            },
            onFailure = { error ->
                Log.e("PermissionSync", "Error al sincronizar permisos de INVAP", error)
            }
        )
    }
}
