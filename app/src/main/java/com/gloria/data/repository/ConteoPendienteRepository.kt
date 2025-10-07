package com.gloria.data.repository

import android.util.Log
import com.gloria.BuildConfig
import com.gloria.data.api.ConteoPendienteApi
import com.gloria.data.model.ConteoPendienteResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository para manejar consultas de conteos pendientes
 */
@Singleton
class ConteoPendienteRepository @Inject constructor(
    private val conteoPendienteApi: ConteoPendienteApi
) {
    
    /**
     * Obtiene conteos pendientes por fechaÏ
     * @param fecha Fecha en formato YYYY-MM-DD
     * @return Result con la respuesta del endpoint
     */
    suspend fun getConteosPendientesByDate(fecha: String): Result<ConteoPendienteResponse> {
        return withContext(Dispatchers.IO) {
            try {
                // Token desde BuildConfig
                val token = BuildConfig.TOKEN_BACKEND
                
                Log.d("CONTEO_PENDIENTE_LOG", "🔍 [REPOSITORY] Consultando conteos pendientes para fecha: $fecha")
                Log.d("CONTEO_PENDIENTE_LOG", "🔍 [REPOSITORY] Usando token: ${token.take(20)}...")
                
                val response = conteoPendienteApi.getConteosPendientesByDate(fecha, token)
                
                Log.d("CONTEO_PENDIENTE_LOG", "✅ [REPOSITORY] Respuesta recibida exitosamente")
                Log.d("CONTEO_PENDIENTE_LOG", "📊 [REPOSITORY] Total inventarios: ${response.totalInventories}")
                Log.d("CONTEO_PENDIENTE_LOG", "📊 [REPOSITORY] Total registros: ${response.totalRecords}")
                Log.d("CONTEO_PENDIENTE_LOG", "📅 [REPOSITORY] Fecha consultada: ${response.date}")
                Log.d("CONTEO_PENDIENTE_LOG", "📋 [REPOSITORY] Inventarios encontrados: ${response.inventories.size}")
                
                // Log de cada inventario
                response.inventories.forEachIndexed { index, inventario ->
                    Log.d("CONTEO_PENDIENTE_LOG", "📦 [REPOSITORY] Inventario ${index + 1}: #${inventario.header.winvdNroInv}")
                    Log.d("CONTEO_PENDIENTE_LOG", "   - Usuario: ${inventario.header.winveLogin}")
                    Log.d("CONTEO_PENDIENTE_LOG", "   - Sucursal: ${inventario.header.sucursal}")
                    Log.d("CONTEO_PENDIENTE_LOG", "   - Depósito: ${inventario.header.deposito}")
                    Log.d("CONTEO_PENDIENTE_LOG", "   - Registros: ${inventario.totalRecords}")
                }
                
                Result.success(response)
                
            } catch (e: Exception) {
                Log.e("CONTEO_PENDIENTE_LOG", "❌ [REPOSITORY] Error al consultar conteos pendientes: ${e.message}")
                Result.failure(e)
            }
        }
    }
}
