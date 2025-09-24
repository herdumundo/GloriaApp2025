package com.gloria.data.dao

import android.util.Log
import com.gloria.domain.usecase.AuthSessionUseCase
import com.gloria.util.ConnectionOracle
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import javax.inject.Inject

/**
 * DAO para obtener permisos de usuario desde Oracle
 */
class UserPermissionOracleDao @Inject constructor(
    private val authSessionUseCase: AuthSessionUseCase
) {
    
    /**
     * Obtiene los permisos de un usuario específico desde Oracle
     * Consulta: SELECT DISTINCT formulario, nombre FROM v_web_operador_rol_prog WHERE login_o = ?
     */
    suspend fun getUserPermissionsFromOracle(username: String): Result<List<Pair<String, String>>> {
        return try {
            val connection = ConnectionOracle.getConnection(authSessionUseCase)
            if (connection == null) {
                return Result.failure(Exception("No se pudo establecer conexión con Oracle"))
            }
            
            // Convertir username a mayúsculas para la consulta Oracle
            val usernameUpper = username.uppercase()
            
            val query = """
                SELECT DISTINCT formulario, nombre 
                FROM v_web_operador_rol_prog 
                WHERE login_o = ?
                ORDER BY formulario
            """
            
            connection.use { conn ->
                conn.prepareStatement(query).use { stmt ->
                    stmt.setString(1, usernameUpper)
                    
                    stmt.executeQuery().use { rs ->
                        val permissions = mutableListOf<Pair<String, String>>()

                        Log.d("UserPermissionOracleDao", "Ejecutando consulta para usuario: $username -> $usernameUpper")
                        Log.d("UserPermissionOracleDao", "Consulta: $query")
                        
                        while (rs.next()) {
                            val formulario = rs.getString("formulario")
                            val nombre = rs.getString("nombre")
                            Log.d("UserPermissionOracleDao", "Encontrado: formulario=$formulario, nombre=$nombre")
                            permissions.add(formulario to nombre)
                        }
                        
                        Log.d("UserPermissionOracleDao", "Total permisos encontrados en Oracle: ${permissions.size}")
                        Log.d("UserPermissionOracleDao", "Permisos: $permissions")
                        
                        Result.success(permissions)
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene todos los usuarios que tienen permisos en Oracle
     */
    suspend fun getAllUsersWithPermissions(): Result<List<String>> {
        return try {
            val connection = ConnectionOracle.getConnection(authSessionUseCase)
            if (connection == null) {
                return Result.failure(Exception("No se pudo establecer conexión con Oracle"))
            }
            
            val query = """
                SELECT DISTINCT login_o 
                FROM v_web_operador_rol_prog 
                ORDER BY login_o
            """
            
            connection.use { conn ->
                conn.prepareStatement(query).use { stmt ->
                    stmt.executeQuery().use { rs ->
                        val users = mutableListOf<String>()
                        
                        while (rs.next()) {
                            val username = rs.getString("login_o")
                            if (username != null && username.isNotBlank()) {
                                // Mantener el username en mayúsculas como está en Oracle
                                users.add(username.uppercase())
                            }
                        }
                        
                        Result.success(users)
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Verifica si un usuario tiene un permiso específico en Oracle
     */
    suspend fun hasPermissionInOracle(username: String, formulario: String): Result<Boolean> {
        return try {
            val connection = ConnectionOracle.getConnection(authSessionUseCase)
            if (connection == null) {
                return Result.failure(Exception("No se pudo establecer conexión con Oracle"))
            }
            
            // Convertir username a mayúsculas para la consulta Oracle
            val usernameUpper = username.uppercase()
            
            val query = """
                SELECT COUNT(*) as count 
                FROM v_web_operador_rol_prog 
                WHERE login_o = ? AND formulario = ?
            """
            
            connection.use { conn ->
                conn.prepareStatement(query).use { stmt ->
                    stmt.setString(1, usernameUpper)
                    stmt.setString(2, formulario)
                    
                    stmt.executeQuery().use { rs ->
                        if (rs.next()) {
                            val count = rs.getInt("count")
                            Result.success(count > 0)
                        } else {
                            Result.success(false)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
