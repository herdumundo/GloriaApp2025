package com.gloria.repository

import android.util.Log
import com.gloria.data.dao.LoggedUserDao
import com.gloria.data.entity.LoggedUser
import com.gloria.util.ConnectionOracle
import com.gloria.util.Controles
import com.gloria.util.Variables
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * Repositorio para manejar operaciones de autenticación con Oracle
 */
class AuthRepository(
    private val loggedUserDao: LoggedUserDao
) {
    
    /**
     * Autentica un usuario usando la base de datos Oracle
     */
    suspend fun authenticateUser(username: String, password: String): AuthResult {
        var connection: Connection? = null
        var statement: PreparedStatement? = null
        var resultSet: ResultSet? = null
        
        Log.d("AuthRepository", "=== INICIANDO authenticateUser ===")
        Log.d("AuthRepository", "Username: $username")
        Log.d("AuthRepository", "Password: ${password.take(3)}***")
        
        try {
            // Establecer las credenciales para la conexión
            Variables.userdb = username
            Variables.passdb = password
            Log.d("AuthRepository", "Credenciales establecidas en Variables")
            
            // Obtener conexión
            Log.d("AuthRepository", "Intentando obtener conexión Oracle...")
            connection = ConnectionOracle.getConnection()
            
            if (connection == null) {
                Log.e("AuthRepository", "❌ CONEXIÓN FALLIDA - connection es null")
                Log.e("AuthRepository", "Controles.resBD: ${Controles.resBD}")
                Log.e("AuthRepository", "Controles.mensajeLogin: ${Controles.mensajeLogin}")
                return when (Controles.resBD) {
                    Controles.ERROR_RED -> AuthResult.NetworkError(Controles.mensajeLogin)
                    Controles.ERROR_CREDENCIALES -> AuthResult.InvalidCredentials(Controles.mensajeLogin)
                    else -> AuthResult.Error("Error de conexión desconocido")
                }
            }
            
            Log.d("AuthRepository", "✅ CONEXIÓN EXITOSA - Usuario autenticado correctamente")
            
            // Guardar el usuario logueado en la base de datos local
            try {
                val loggedUser = LoggedUser(
                    id = 1,
                    username = username,
                    password = password,
                    loginTimestamp = System.currentTimeMillis()
                )
                Log.d("AuthRepository", "💾 Guardando usuario en base de datos local...")
                loggedUserDao.insertLoggedUser(loggedUser)
                Log.d("AuthRepository", "✅ Usuario guardado: $username")
            } catch (e: Exception) {
                Log.e("AuthRepository", "❌ Error al guardar usuario: ${e.message}")
            }
            
            // Si llegamos aquí, la conexión fue exitosa
            return AuthResult.Success(username)
            
        } catch (e: Exception) {
            Log.e("AuthRepository", "❌ ERROR en authenticateUser: ${e.message}")
            Log.e("AuthRepository", "Stack trace: ${e.stackTraceToString()}")
            return AuthResult.Error("Error durante la autenticación: ${e.message}")
        } finally {
            // Cerrar recursos
            try {
                resultSet?.close()
                statement?.close()
                connection?.close()
                Log.d("AuthRepository", "🔒 Recursos cerrados correctamente")
            } catch (e: Exception) {
                Log.e("AuthRepository", "Error al cerrar recursos: ${e.message}")
            }
        }
    }
    
    /**
     * Registra un nuevo usuario en la base de datos
     */
    fun registerUser(username: String, password: String): AuthResult {
        var connection: Connection? = null
        var statement: PreparedStatement? = null
        
        try {
            // Primero verificar si el usuario ya existe
            if (userExists(username)) {
                return AuthResult.Error("El usuario ya existe")
            }
            
            // Aquí puedes implementar la lógica para insertar el usuario
            // Por ahora, solo verificamos que la conexión funcione
            connection = ConnectionOracle.getConnection()
            
                        if (connection == null) {
                return when (Controles.resBD) {
                    Controles.ERROR_RED -> AuthResult.NetworkError(Controles.mensajeLogin)
                    Controles.ERROR_CREDENCIALES -> AuthResult.InvalidCredentials(Controles.mensajeLogin)
                    else -> AuthResult.Error("Error de conexión desconocido")
                }
            }
            
            // TODO: Implementar INSERT INTO usuarios (username, password) VALUES (?, ?)
            // Por ahora simulamos el registro exitoso
            return AuthResult.Success(username)
            
        } catch (e: Exception) {
            return AuthResult.Error("Error durante el registro: ${e.message}")
        } finally {
            try {
                statement?.close()
                connection?.close()
            } catch (e: Exception) {
                // Log del error al cerrar recursos
            }
        }
    }
    
    /**
     * Prueba la conexión a Oracle con credenciales específicas
     */
    fun testConnection(testUsername: String, testPassword: String): AuthResult {
        try {
            // Establecer las credenciales para la prueba
            Variables.userdb = testUsername
            Variables.passdb = testPassword
            
            // Obtener conexión
            val connection = ConnectionOracle.getConnection()
            
            if (connection == null) {
                return when (Controles.resBD) {
                    Controles.ERROR_RED -> AuthResult.NetworkError(Controles.mensajeLogin)
                    Controles.ERROR_CREDENCIALES -> AuthResult.InvalidCredentials(Controles.mensajeLogin)
                    else -> AuthResult.Error("Error de conexión desconocido")
                }
            }
            
            // Si llegamos aquí, la conexión fue exitosa
            connection.close()
            return AuthResult.Success("Conexión exitosa a Oracle")
            
        } catch (e: Exception) {
            return AuthResult.Error("Error durante la prueba de conexión: ${e.message}")
        }
    }
    
    /**
     * Cierra la sesión del usuario
     */
    suspend fun logout() {
        try {
            Log.d("AuthRepository", "🚪 Cerrando sesión del usuario...")
            loggedUserDao.clearLoggedUsers()
            Log.d("AuthRepository", "✅ Sesión cerrada correctamente")
        } catch (e: Exception) {
            Log.e("AuthRepository", "❌ Error al cerrar sesión: ${e.message}")
        }
    }
    
    /**
     * Obtiene el usuario logueado
     */
    suspend fun getLoggedUser(): com.gloria.data.entity.LoggedUser? {
        // Esta implementación debería usar un DAO inyectado
        // Por ahora retornamos null, se implementará en el módulo DI
        return null
    }
    
    /**
     * Verifica si un usuario ya existe
     */
    private fun userExists(username: String): Boolean {
        var connection: Connection? = null
        var statement: PreparedStatement? = null
        var resultSet: ResultSet? = null
        
        try {
            // Usar credenciales de administrador para verificar existencia
            // Variables.userdb = "admin_user" // Usuario con permisos de lectura
            // Variables.passdb = "admin_password"
            
            connection = ConnectionOracle.getConnection()
            
            if (connection == null) {
                return false
            }
            
            // TODO: Implementar SELECT COUNT(*) FROM usuarios WHERE username = ?
            // Por ahora simulamos que no existe
            return false
            
        } catch (e: Exception) {
            return false
        } finally {
            try {
                resultSet?.close()
                statement?.close()
                connection?.close()
            } catch (e: Exception) {
                // Log del error al cerrar recursos
            }
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
