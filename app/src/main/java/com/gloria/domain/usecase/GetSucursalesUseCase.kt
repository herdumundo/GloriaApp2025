package com.gloria.domain.usecase

import com.gloria.data.repository.LoggedUserRepository
import com.gloria.data.entity.LoggedUser
import com.gloria.repository.SucursalRepository
import com.gloria.repository.SucursalResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first
import android.util.Log
import javax.inject.Inject

/**
 * Caso de uso para obtener las sucursales del usuario logueado
 * Reutilizable para diferentes partes de la aplicación
 */
class GetSucursalesUseCase @Inject constructor(
    private val loggedUserRepository: LoggedUserRepository
) {
    
    /**
     * Ejecuta el caso de uso para obtener las sucursales
     * @return SucursalResult con la lista de sucursales o error
     */
    suspend operator fun invoke(): SucursalResult = withContext(Dispatchers.IO) {
        Log.d("PROCESO_LOGIN", "=== INICIANDO GetSucursalesUseCase ===")
        Log.d("PROCESO_LOGIN", "🔄 Ejecutando en hilo IO: ${Thread.currentThread().name}")
        
        try {
            // Obtener el usuario logueado de la base de datos local
            Log.d("PROCESO_LOGIN", "🔍 Obteniendo usuario logueado de la BD local...")
            val loggedUser = loggedUserRepository.getLoggedUserSync()
            
            if (loggedUser == null) {
                Log.e("PROCESO_LOGIN", "❌ No hay usuario logueado en la BD local")
                return@withContext SucursalResult.Error("No hay usuario logueado")
            }
            
            Log.d("PROCESO_LOGIN", "✅ Usuario logueado encontrado:")
            Log.d("PROCESO_LOGIN", "   - Username: ${loggedUser.username}")
            Log.d("PROCESO_LOGIN", "   - Password: ${loggedUser.password.take(3)}***")
            Log.d("PROCESO_LOGIN", "   - Login Timestamp: ${loggedUser.loginTimestamp}")
            
            // Crear repository localmente
            Log.d("PROCESO_LOGIN", "🏗️ Creando SucursalRepository...")
            val sucursalRepository = SucursalRepository()
            
            // Usar las credenciales del usuario logueado para consultar Oracle
            Log.d("PROCESO_LOGIN", "🔍 Consultando sucursales en Oracle...")
            val result = sucursalRepository.getSucursales(
                username = loggedUser.username,
                password = loggedUser.password
            )
            
            Log.d("PROCESO_LOGIN", "📊 Resultado de la consulta:")
            when (result) {
                is SucursalResult.Success -> {
                    Log.d("PROCESO_LOGIN", "✅ ÉXITO - ${result.sucursales.size} sucursales obtenidas")
                    result.sucursales.forEachIndexed { index, sucursal ->
                        Log.d("PROCESO_LOGIN", "   Sucursal ${index + 1}: ${sucursal.descripcion} - Rol: ${sucursal.rol}")
                    }
                }
                is SucursalResult.Error -> {
                    Log.e("PROCESO_LOGIN", "❌ ERROR: ${result.message}")
                }
                is SucursalResult.NetworkError -> {
                    Log.e("PROCESO_LOGIN", "🌐 ERROR DE RED: ${result.message}")
                }
                is SucursalResult.InvalidCredentials -> {
                    Log.e("PROCESO_LOGIN", "🔐 CREDENCIALES INVÁLIDAS: ${result.message}")
                }
            }
            
            return@withContext result
            
        } catch (e: Exception) {
            Log.e("PROCESO_LOGIN", "❌ EXCEPCIÓN en GetSucursalesUseCase: ${e.message}")
            Log.e("PROCESO_LOGIN", "Stack trace: ${e.stackTraceToString()}")
            return@withContext SucursalResult.Error("Error al obtener sucursales: ${e.message}")
        }
    }
}
