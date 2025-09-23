package com.gloria.repository

import android.util.Log
import com.gloria.domain.model.Sucursal
import com.gloria.util.ConnectionOracle
import com.gloria.util.Controles
import com.gloria.util.Variables
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    suspend fun getSucursales(username: String, password: String): SucursalResult = withContext(Dispatchers.IO) {
        var connection: Connection? = null
        var statement: PreparedStatement? = null
        var resultSet: ResultSet? = null
        
        Log.d("PROCESO_LOGIN", "=== INICIANDO getSucursales ===")
        Log.d("PROCESO_LOGIN", "🔄 Ejecutando en hilo IO: ${Thread.currentThread().name}")
        Log.d("PROCESO_LOGIN", "Username: $username")
        Log.d("PROCESO_LOGIN", "Password: ${password.take(3)}***")
        
        try {
            // Establecer las credenciales para la conexión
            Variables.userdb = username
            Variables.passdb = password
            Log.d("PROCESO_LOGIN", "Credenciales establecidas en Variables")
            
            // Obtener conexión
            Log.d("PROCESO_LOGIN", "Intentando obtener conexión Oracle...")
            connection = ConnectionOracle.getConnection()
            
            if (connection == null) {
                Log.e("PROCESO_LOGIN", "❌ CONEXIÓN FALLIDA - connection es null")
                Log.e("PROCESO_LOGIN", "Controles.resBD: ${Controles.resBD}")
                Log.e("PROCESO_LOGIN", "Controles.mensajeLogin: ${Controles.mensajeLogin}")
                return@withContext when (Controles.resBD) {
                    Controles.ERROR_RED -> SucursalResult.NetworkError(Controles.mensajeLogin)
                    Controles.ERROR_CREDENCIALES -> SucursalResult.InvalidCredentials(Controles.mensajeLogin)
                    else -> SucursalResult.Error("Error de conexión desconocido")
                }
            }
            
            Log.d("PROCESO_LOGIN", "✅ CONEXIÓN EXITOSA - connection obtenida")
            
            // Consulta SQL para obtener las sucursales
            val sql = """
                SELECT DISTINCT SUCURSAL_DESCRIPCION, ROL_SUCURSAL 
                FROM v_web_operador_rol_prog 
                WHERE login_o = ? 
                ORDER BY 2
            """.trimIndent()
            
            Log.d("PROCESO_LOGIN", "🔍 Ejecutando consulta SQL:")
            Log.d("PROCESO_LOGIN", "SQL: $sql")
            Log.d("PROCESO_LOGIN", "Parámetro: ${username.uppercase()}")
            
            statement = connection.prepareStatement(sql)
            statement.setString(1, username.uppercase())
            
            Log.d("PROCESO_LOGIN", "📊 Ejecutando query...")
            resultSet = statement.executeQuery()
            
            val sucursales = mutableListOf<Sucursal>()
            var contador = 0
            while (resultSet.next()) {
                val descripcion = resultSet.getString("SUCURSAL_DESCRIPCION")
                val rolSucursal = resultSet.getString("ROL_SUCURSAL")
                // Convertir ROL_SUCURSAL a Int para usar como ID
                val codigoSucursal = try {
                    rolSucursal.toInt()
                } catch (e: NumberFormatException) {
                    // Si no se puede convertir a Int, usar el hash del string como ID
                    rolSucursal.hashCode()
                }
                sucursales.add(Sucursal(codigoSucursal, descripcion, rolSucursal))
                contador++
                Log.d("PROCESO_LOGIN", "Sucursal $contador: $codigoSucursal - $descripcion - Rol: $rolSucursal")
            }
            
            Log.d("PROCESO_LOGIN", "📈 Total sucursales encontradas: $contador")
            
            if (sucursales.isEmpty()) {
                Log.w("PROCESO_LOGIN", "⚠️ No se encontraron sucursales para este usuario")
                return@withContext SucursalResult.Error("No se encontraron sucursales para este usuario")
            }
            
            Log.d("PROCESO_LOGIN", "✅ ÉXITO - Retornando ${sucursales.size} sucursales")
            return@withContext SucursalResult.Success(sucursales)
            
        } catch (e: Exception) {
            Log.e("PROCESO_LOGIN", "❌ ERROR en getSucursales: ${e.message}")
            Log.e("PROCESO_LOGIN", "Stack trace: ${e.stackTraceToString()}")
            return@withContext SucursalResult.Error("Error al obtener sucursales: ${e.message}")
        } finally {
            try {
                resultSet?.close()
                statement?.close()
                connection?.close()
                Log.d("PROCESO_LOGIN", "🔒 Recursos cerrados correctamente")
            } catch (e: Exception) {
                Log.e("PROCESO_LOGIN", "Error al cerrar recursos: ${e.message}")
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
