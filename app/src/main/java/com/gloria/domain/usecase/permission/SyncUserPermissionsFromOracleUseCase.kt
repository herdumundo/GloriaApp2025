package com.gloria.domain.usecase.permission

import android.util.Log
import com.gloria.data.repository.UserPermissionRepository
import com.gloria.data.repository.OracleLoginApiRepository
import javax.inject.Inject

/**
 * UseCase para sincronizar permisos de usuario desde API a Room
 */
class SyncUserPermissionsFromOracleUseCase @Inject constructor(
    private val oracleLoginApiRepository: OracleLoginApiRepository,
    private val userPermissionRepository: UserPermissionRepository
) {
    
    /**
     * Sincroniza los permisos de un usuario específico desde la API
     */
    suspend operator fun invoke(username: String, password: String): Result<Unit> {
        return try {
            Log.d("UserPermissions", "=== SINCRONIZANDO PERMISOS DE $username DESDE API UNIFICADA ===")
            
            // Llamar a la API de login unificada para obtener permisos
            val apiResult = oracleLoginApiRepository.oracleLogin(username, password)
            
            apiResult.fold(
                onSuccess = { loginResponse ->
                    Log.d("SyncUserPermissionsFromOracleUseCase", "Respuesta de API para $username: ${loginResponse.permisos}")
                    
                    if (loginResponse.permisos.isNotEmpty()) {
                        Log.d("SyncUserPermissionsFromOracleUseCase", "Permisos encontrados en API: ${loginResponse.permisos}")
                        
                        // Convertir permisos de API a formato esperado por Room
                        val permissions = loginResponse.permisos.map { permiso ->
                            permiso.formulario to permiso.nombre
                        }
                        
                        // Sincronizar con Room
                        Log.d("SyncUserPermissionsFromOracleUseCase", "Llamando a syncUserPermissionsFromOracle...")
                        userPermissionRepository.syncUserPermissionsFromOracle(username, permissions)
                        
                        Log.d("SyncUserPermissionsFromOracleUseCase", "Permisos sincronizados exitosamente para $username")
                        Result.success(Unit)
                    } else {
                        Log.w("SyncUserPermissionsFromOracleUseCase", "No se encontraron permisos en API para $username")
                        // Eliminar permisos existentes en Room si no hay en API
                        userPermissionRepository.deleteAllUserPermissions(username)
                        Result.success(Unit)
                    }
                },
                onFailure = { error ->
                    Log.e("UserPermissions", "Error al obtener permisos desde API para $username", error)
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Log.e("UserPermissions", "Error inesperado al sincronizar permisos para $username", e)
            Result.failure(e)
        }
    }
    
    /**
     * Verifica si un usuario tiene un permiso específico en la API y sincroniza si es necesario
     */
    suspend fun checkAndSyncUserPermission(username: String, password: String, formulario: String): Result<Boolean> {
        return try {
            val apiResult = oracleLoginApiRepository.oracleLogin(username, password)
            
            apiResult.fold(
                onSuccess = { loginResponse ->
                    val hasPermission = loginResponse.permisos.any { it.formulario == formulario }
                    
                    // Si tiene el permiso en API, sincronizar todos sus permisos
                    if (hasPermission) {
                        invoke(username, password)
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
