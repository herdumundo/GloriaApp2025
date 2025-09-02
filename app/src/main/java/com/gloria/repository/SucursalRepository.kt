package com.gloria.repository

import android.util.Log
import com.gloria.domain.model.Sucursal
import com.gloria.util.ConnectionOracle
import com.gloria.util.Controles
import com.gloria.util.Variables
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
        
        Log.d("SucursalRepository", "=== INICIANDO getSucursales ===")
        Log.d("SucursalRepository", "Username: $username")
        Log.d("SucursalRepository", "Password: ${password.take(3)}***")
        
        try {
            // Establecer las credenciales para la conexión
            Variables.userdb = username
            Variables.passdb = password
            Log.d("SucursalRepository", "Credenciales establecidas en Variables")
            
            // Obtener conexión
            Log.d("SucursalRepository", "Intentando obtener conexión Oracle...")
            connection = ConnectionOracle.getConnection()
            
            if (connection == null) {
                Log.e("SucursalRepository", "❌ CONEXIÓN FALLIDA - connection es null")
                Log.e("SucursalRepository", "Controles.resBD: ${Controles.resBD}")
                Log.e("SucursalRepository", "Controles.mensajeLogin: ${Controles.mensajeLogin}")
                return when (Controles.resBD) {
                    Controles.ERROR_RED -> SucursalResult.NetworkError(Controles.mensajeLogin)
                    Controles.ERROR_CREDENCIALES -> SucursalResult.InvalidCredentials(Controles.mensajeLogin)
                    else -> SucursalResult.Error("Error de conexión desconocido")
                }
            }
            
            Log.d("SucursalRepository", "✅ CONEXIÓN EXITOSA - connection obtenida")
            
            // Consulta SQL para obtener las sucursales
            val sql = """
                SELECT DISTINCT SUCURSAL_DESCRIPCION, ROL_SUCURSAL 
                FROM v_web_operador_rol_prog 
                WHERE login_o = ? 
                ORDER BY 2
            """.trimIndent()
            
            Log.d("SucursalRepository", "🔍 Ejecutando consulta SQL:")
            Log.d("SucursalRepository", "SQL: $sql")
            Log.d("SucursalRepository", "Parámetro: ${username.uppercase()}")
            
            statement = connection.prepareStatement(sql)
            statement.setString(1, username.uppercase())
            
            Log.d("SucursalRepository", "📊 Ejecutando query...")
            resultSet = statement.executeQuery()
            
            val sucursales = mutableListOf<Sucursal>()
            var contador = 0
            while (resultSet.next()) {
                val descripcion = resultSet.getString("SUCURSAL_DESCRIPCION")
                val rol = resultSet.getString("ROL_SUCURSAL")
                sucursales.add(Sucursal(descripcion, rol))
                contador++
                Log.d("SucursalRepository", "Sucursal $contador: $descripcion - Rol: $rol")
            }
            
            Log.d("SucursalRepository", "📈 Total sucursales encontradas: $contador")
            
            if (sucursales.isEmpty()) {
                Log.w("SucursalRepository", "⚠️ No se encontraron sucursales para este usuario")
                return SucursalResult.Error("No se encontraron sucursales para este usuario")
            }
            
            Log.d("SucursalRepository", "✅ ÉXITO - Retornando ${sucursales.size} sucursales")
            return SucursalResult.Success(sucursales)
            
        } catch (e: Exception) {
            Log.e("SucursalRepository", "❌ ERROR en getSucursales: ${e.message}")
            Log.e("SucursalRepository", "Stack trace: ${e.stackTraceToString()}")
            return SucursalResult.Error("Error al obtener sucursales: ${e.message}")
        } finally {
            try {
                resultSet?.close()
                statement?.close()
                connection?.close()
                Log.d("SucursalRepository", "🔒 Recursos cerrados correctamente")
            } catch (e: Exception) {
                Log.e("SucursalRepository", "Error al cerrar recursos: ${e.message}")
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
