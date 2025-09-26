package com.gloria.repository

import android.util.Log
import com.gloria.data.repository.OracleLoginApiRepository
import com.gloria.domain.model.Sucursal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio para manejar las consultas de sucursales usando API
 */
class SucursalRepository(
    private val oracleLoginApiRepository: OracleLoginApiRepository
) {
    
    /**
     * Obtiene las sucursales disponibles para un usuario usando la API
     */
    suspend fun getSucursales(username: String, password: String): SucursalResult = withContext(Dispatchers.IO) {
        Log.d("PROCESO_LOGIN", "=== INICIANDO getSucursales con API ===")
        Log.d("PROCESO_LOGIN", "🔄 Ejecutando en hilo IO: ${Thread.currentThread().name}")
        Log.d("PROCESO_LOGIN", "Username: $username")
        
        try {
            Log.d("PROCESO_LOGIN", "🌐 Llamando a API de login para obtener sucursales...")
            
            // Llamar a la API para obtener login con sucursales
            val apiResult = oracleLoginApiRepository.oracleLogin(username, password)
            
            if (apiResult.isFailure) {
                Log.e("PROCESO_LOGIN", "❌ ERROR en API: ${apiResult.exceptionOrNull()?.message}")
                return@withContext SucursalResult.Error("Error al obtener sucursales: ${apiResult.exceptionOrNull()?.message}")
            }
            
            val loginResponse = apiResult.getOrNull()!!
            Log.d("PROCESO_LOGIN", "✅ Respuesta recibida de API")
            Log.d("PROCESO_LOGIN", "Sucursales encontradas: ${loginResponse.sucursales.size}")
            
            // Convertir sucursales de API a modelo de dominio
            val sucursales = loginResponse.sucursales.map { sucursalApi ->
                Sucursal(
                    id = sucursalApi.codigo,
                    descripcion = sucursalApi.descripcion,
                    rol = sucursalApi.codigo.toString()
                )
            }
            
            Log.d("PROCESO_LOGIN", "📈 Total sucursales convertidas: ${sucursales.size}")
            
            if (sucursales.isEmpty()) {
                Log.w("PROCESO_LOGIN", "⚠️ No se encontraron sucursales para este usuario")
                return@withContext SucursalResult.Error("No se encontraron sucursales para este usuario")
            }
            
            // Log de sucursales encontradas
            sucursales.forEachIndexed { index, sucursal ->
                Log.d("PROCESO_LOGIN", "Sucursal ${index + 1}: ${sucursal.id} - ${sucursal.descripcion} - Rol: ${sucursal.rol}")
            }
            
            Log.d("PROCESO_LOGIN", "✅ ÉXITO - Retornando ${sucursales.size} sucursales")
            return@withContext SucursalResult.Success(sucursales)
            
        } catch (e: Exception) {
            Log.e("PROCESO_LOGIN", "❌ ERROR en getSucursales API: ${e.message}")
            Log.e("PROCESO_LOGIN", "Stack trace: ${e.stackTraceToString()}")
            return@withContext SucursalResult.Error("Error al obtener sucursales: ${e.message}")
        }
    }
}

/**
 * Resultado de las operaciones de sucursales
 */
sealed class SucursalResult {
    data class Success(val sucursales: List<Sucursal>) : SucursalResult()
    data class Error(val message: String) : SucursalResult()
}