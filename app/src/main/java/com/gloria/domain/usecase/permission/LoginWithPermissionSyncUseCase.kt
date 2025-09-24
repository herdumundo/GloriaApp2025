package com.gloria.domain.usecase.permission

import android.util.Log
import com.gloria.domain.usecase.auth.LoginUseCase
import com.gloria.repository.AuthResult
import com.gloria.domain.usecase.permission.SyncUserPermissionsFromOracleUseCase
import javax.inject.Inject

/**
 * UseCase que extiende el login para sincronizar automáticamente los permisos del usuario
 */
class LoginWithPermissionSyncUseCase @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val syncUserPermissionsFromOracleUseCase: SyncUserPermissionsFromOracleUseCase
) {
    
    /**
     * Realiza login y sincroniza los permisos del usuario desde Oracle
     */
    suspend operator fun invoke(username: String, password: String): AuthResult {
        return try {
            Log.d("LoginWithPermissionSync", "=== INICIANDO LOGIN CON SINCRONIZACIÓN DE PERMISOS ===")
            Log.d("LoginWithPermissionSync", "Usuario: $username")
            
            // 1. Realizar login normal
            val loginResult = loginUseCase(username, password)
            
            when (loginResult) {
                is AuthResult.Success -> {
                    Log.d("LoginWithPermissionSync", "Login exitoso, sincronizando permisos...")
                    
                    // 2. Sincronizar permisos desde Oracle
                    val syncResult = syncUserPermissionsFromOracleUseCase(username)
                    
                    syncResult.fold(
                        onSuccess = {
                            Log.d("LoginWithPermissionSync", "Permisos sincronizados exitosamente para $username")
                        },
                        onFailure = { error ->
                            Log.e("LoginWithPermissionSync", "Error al sincronizar permisos para $username", error)
                            // No fallar el login si hay error en sincronización de permisos
                            // El usuario puede usar la app sin permisos sincronizados
                        }
                    )
                    
                    loginResult // Retornar el resultado exitoso del login
                }
                is AuthResult.Error -> {
                    Log.e("LoginWithPermissionSync", "Error en login para $username: ${loginResult.message}")
                    loginResult
                }
                is AuthResult.NetworkError -> {
                    Log.e("LoginWithPermissionSync", "Error de red en login para $username: ${loginResult.message}")
                    loginResult
                }
                is AuthResult.InvalidCredentials -> {
                    Log.e("LoginWithPermissionSync", "Credenciales inválidas para $username: ${loginResult.message}")
                    loginResult
                }
            }
        } catch (e: Exception) {
            Log.e("LoginWithPermissionSync", "Error inesperado en login con sincronización", e)
            AuthResult.Error("Error inesperado durante el login: ${e.message}")
        }
    }
}
