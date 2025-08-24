package com.gloria.repository

import com.gloria.domain.model.Sucursal
import com.gloria.util.ConnectionOracle
import com.gloria.util.Controles
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * Repositorio para manejar las consultas de sucursales
 */
class SucursalRepository {
    
    /**
     * Obtiene las sucursales disponibles para un usuario
     */
    fun getSucursales(username: String, password: String): SucursalResult {
        var connection: Connection? = null
        var statement: PreparedStatement? = null
        var resultSet: ResultSet? = null
        
        try {
            // Establecer las credenciales para la conexión
            com.gloria.util.Variables.userdb = username
            com.gloria.util.Variables.passdb = password
            
            // Obtener conexión
            connection = ConnectionOracle.getConnection()
            
            if (connection == null) {
                return when (Controles.resBD) {
                    Controles.ERROR_RED -> SucursalResult.NetworkError(Controles.mensajeLogin)
                    Controles.ERROR_CREDENCIALES -> SucursalResult.InvalidCredentials(Controles.mensajeLogin)
                    else -> SucursalResult.Error("Error de conexión desconocido")
                }
            }
            
            // Consulta SQL para obtener las sucursales
            val sql = """
                SELECT DISTINCT SUCURSAL_DESCRIPCION, ROL_SUCURSAL 
                FROM v_web_operador_rol_prog 
                WHERE login_o = ? 
                ORDER BY 2
            """.trimIndent()
            
            statement = connection.prepareStatement(sql)
            statement.setString(1, username.uppercase())
            
            resultSet = statement.executeQuery()
            
            val sucursales = mutableListOf<Sucursal>()
            while (resultSet.next()) {
                val descripcion = resultSet.getString("SUCURSAL_DESCRIPCION")
                val rol = resultSet.getString("ROL_SUCURSAL")
                sucursales.add(Sucursal(descripcion, rol))
            }
            
            if (sucursales.isEmpty()) {
                return SucursalResult.Error("No se encontraron sucursales para este usuario")
            }
            
            return SucursalResult.Success(sucursales)
            
        } catch (e: Exception) {
            return SucursalResult.Error("Error al obtener sucursales: ${e.message}")
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
 * Resultado de las operaciones de sucursales
 */
sealed class SucursalResult {
    data class Success(val sucursales: List<Sucursal>) : SucursalResult()
    data class Error(val message: String) : SucursalResult()
    data class NetworkError(val message: String) : SucursalResult()
    data class InvalidCredentials(val message: String) : SucursalResult()
}
