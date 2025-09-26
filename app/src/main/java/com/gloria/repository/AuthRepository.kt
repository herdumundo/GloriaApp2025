package com.gloria.repository

import android.util.Log
import com.gloria.data.repository.LoggedUserRepository
import com.gloria.data.repository.OracleLoginApiRepository
import com.gloria.data.entity.LoggedUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio para manejar operaciones de autenticación con API
 */
class AuthRepository(
    private val loggedUserRepository: LoggedUserRepository,
    private val oracleLoginApiRepository: OracleLoginApiRepository
) {
    
    /**
     * Autentica un usuario usando la API
     */
    suspend fun authenticateUser(username: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        Log.d("PROCESO_LOGIN", "=== INICIANDO authenticateUser con API ===")
        Log.d("PROCESO_LOGIN", "Username: $username")
        Log.d("PROCESO_LOGIN", "Password: ${password.take(3)}***")
        Log.d("PROCESO_LOGIN", "🔄 Ejecutando en hilo IO: ${Thread.currentThread().name}")
        
        try {
            Log.d("PROCESO_LOGIN", "🌐 Llamando a API de login...")
            
            // Llamar a la API para autenticar
            val apiResult = oracleLoginApiRepository.oracleLogin(username, password)
            
            if (apiResult.isFailure) {
                Log.e("PROCESO_LOGIN", "❌ ERROR en API: ${apiResult.exceptionOrNull()?.message}")
                return@withContext AuthResult.Error("Error al autenticar: ${apiResult.exceptionOrNull()?.message}")
            }
            
            val loginResponse = apiResult.getOrNull()!!
            Log.d("PROCESO_LOGIN", "✅ Login exitoso desde API")
            Log.d("PROCESO_LOGIN", "Usuario: $username")
            Log.d("PROCESO_LOGIN", "Sucursales: ${loginResponse.sucursales.size}")
            Log.d("PROCESO_LOGIN", "Permisos: ${loginResponse.permisos.size}")
            
            // Guardar el usuario logueado en la base de datos local
            try {
                val loggedUser = LoggedUser(
                    id = 1,
                    username = username,
                    password = password,
                    loginTimestamp = System.currentTimeMillis()
                )
                Log.d("PROCESO_LOGIN", "💾 Guardando usuario en base de datos local...")
                loggedUserRepository.insertLoggedUser(loggedUser)
                Log.d("PROCESO_LOGIN", "✅ Usuario guardado: $username")
            } catch (e: Exception) {
                Log.e("PROCESO_LOGIN", "❌ Error al guardar usuario: ${e.message}")
            }
            
            // Si llegamos aquí, la autenticación fue exitosa
            Log.d("PROCESO_LOGIN", "✅ AUTENTICACIÓN EXITOSA")
            return@withContext AuthResult.Success(loginResponse.message)
            
        } catch (e: Exception) {
            Log.e("PROCESO_LOGIN", "❌ ERROR GENERAL en authenticateUser API: ${e.message}")
            Log.e("PROCESO_LOGIN", "Stack trace: ${e.stackTraceToString()}")
            return@withContext AuthResult.Error("Error durante la autenticación: ${e.message}")
        }
    }
    
    /**
     * Cierra la sesión del usuario
     */
    suspend fun logout() {
        try {
            Log.d("AuthRepository", "🚪 Cerrando sesión del usuario...")
            loggedUserRepository.clearLoggedUsers()
            Log.d("AuthRepository", "✅ Sesión cerrada correctamente")
        } catch (e: Exception) {
            Log.e("AuthRepository", "❌ Error al cerrar sesión: ${e.message}")
        }
    }
}

/**
 * Resultado de las operaciones de autenticación
 */
sealed class AuthResult {
    data class Success(val username: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
    data class NetworkError(val message: String) : AuthResult()
    data class InvalidCredentials(val message: String) : AuthResult()
}