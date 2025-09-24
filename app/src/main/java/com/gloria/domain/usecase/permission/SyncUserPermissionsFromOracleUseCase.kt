package com.gloria.domain.usecase.permission

import android.util.Log
import com.gloria.data.dao.UserPermissionOracleDao
import com.gloria.data.repository.UserPermissionRepository
import javax.inject.Inject

/**
 * UseCase para sincronizar permisos de usuario desde Oracle a Room
 */
class SyncUserPermissionsFromOracleUseCase @Inject constructor(
    private val userPermissionOracleDao: UserPermissionOracleDao,
    private val userPermissionRepository: UserPermissionRepository
) {
    
    /**
     * Sincroniza los permisos de un usuario específico desde Oracle
     */
    suspend operator fun invoke(username: String): Result<Unit> {
        return try {
            Log.d("UserPermissions", "=== SINCRONIZANDO PERMISOS DE $username ===")
            
            // Obtener permisos desde Oracle
            val oracleResult = userPermissionOracleDao.getUserPermissionsFromOracle(username)
            
            oracleResult.fold(
                onSuccess = { permissions ->
                    Log.d("SyncUserPermissionsFromOracleUseCase", "Resultado de Oracle para $username: $permissions")
                    
                    if (permissions.isNotEmpty()) {
                        Log.d("SyncUserPermissionsFromOracleUseCase", "Permisos encontrados en Oracle: $permissions")
                        
                        // Sincronizar con Room
                        Log.d("SyncUserPermissionsFromOracleUseCase", "Llamando a syncUserPermissionsFromOracle...")
                        userPermissionRepository.syncUserPermissionsFromOracle(username, permissions)
                        
                        Log.d("SyncUserPermissionsFromOracleUseCase", "Permisos sincronizados exitosamente para $username")
                        Result.success(Unit)
                    } else {
                        Log.w("SyncUserPermissionsFromOracleUseCase", "No se encontraron permisos en Oracle para $username")
                        // Eliminar permisos existentes en Room si no hay en Oracle
                        userPermissionRepository.deleteAllUserPermissions(username)
                        Result.success(Unit)
                    }
                },
                onFailure = { error ->
                    Log.e("UserPermissions", "Error al obtener permisos desde Oracle para $username", error)
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Log.e("UserPermissions", "Error inesperado al sincronizar permisos para $username", e)
            Result.failure(e)
        }
    }
    
    /**
     * Sincroniza los permisos de todos los usuarios desde Oracle
     */
    suspend fun syncAllUsersPermissions(): Result<Unit> {
        return try {
            Log.d("UserPermissions", "=== SINCRONIZANDO PERMISOS DE TODOS LOS USUARIOS ===")
            
            // Obtener lista de usuarios desde Oracle
            val usersResult = userPermissionOracleDao.getAllUsersWithPermissions()
            
            usersResult.fold(
                onSuccess = { users ->
                    Log.d("UserPermissions", "Usuarios encontrados en Oracle: $users")
                    
                    var successCount = 0
                    var errorCount = 0
                    
                    users.forEach { username ->
                        try {
                            val result = invoke(username)
                            if (result.isSuccess) {
                                successCount++
                            } else {
                                errorCount++
                                Log.e("UserPermissions", "Error al sincronizar usuario $username: ${result.exceptionOrNull()?.message}")
                            }
                        } catch (e: Exception) {
                            errorCount++
                            Log.e("UserPermissions", "Error inesperado al sincronizar usuario $username", e)
                        }
                    }
                    
                    Log.d("UserPermissions", "Sincronización completada: $successCount exitosos, $errorCount errores")
                    Result.success(Unit)
                },
                onFailure = { error ->
                    Log.e("UserPermissions", "Error al obtener lista de usuarios desde Oracle", error)
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Log.e("UserPermissions", "Error inesperado en sincronización masiva", e)
            Result.failure(e)
        }
    }
    
    /**
     * Verifica si un usuario tiene un permiso específico en Oracle y sincroniza si es necesario
     */
    suspend fun checkAndSyncUserPermission(username: String, formulario: String): Result<Boolean> {
        return try {
            val oracleResult = userPermissionOracleDao.hasPermissionInOracle(username, formulario)
            
            oracleResult.fold(
                onSuccess = { hasPermission ->
                    // Si tiene el permiso en Oracle, sincronizar todos sus permisos
                    if (hasPermission) {
                        invoke(username)
                    }
                    Result.success(hasPermission)
                },
                onFailure = { error ->
                    Log.e("UserPermissions", "Error al verificar permiso $formulario para $username", error)
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Log.e("UserPermissions", "Error inesperado al verificar y sincronizar permiso", e)
            Result.failure(e)
        }
    }
}
