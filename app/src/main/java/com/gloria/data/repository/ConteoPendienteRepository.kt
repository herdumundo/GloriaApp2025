package com.gloria.data.repository

import android.util.Log
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
     * Obtiene conteos pendientes por fecha
     * @param fecha Fecha en formato YYYY-MM-DD
     * @return Result con la respuesta del endpoint
     */
    suspend fun getConteosPendientesByDate(fecha: String): Result<ConteoPendienteResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("CONTEO_PENDIENTE_LOG", "🔍 [REPOSITORY] Consultando conteos pendientes para fecha: $fecha")
                
                val response = conteoPendienteApi.getConteosPendientesByDate(fecha)
                
                Log.d("CONTEO_PENDIENTE_LOG", "✅ [REPOSITORY] Respuesta recibida exitosamente")
                Log.d("CONTEO_PENDIENTE_LOG", "📊 [REPOSITORY] Total registros: ${response.totalRecords}")
                Log.d("CONTEO_PENDIENTE_LOG", "📅 [REPOSITORY] Fecha consultada: ${response.date}")
                Log.d("CONTEO_PENDIENTE_LOG", "🏢 [REPOSITORY] Sucursal: ${response.header.sucursal}")
                Log.d("CONTEO_PENDIENTE_LOG", "📦 [REPOSITORY] Depósito: ${response.header.deposito}")
                Log.d("CONTEO_PENDIENTE_LOG", "👤 [REPOSITORY] Usuario: ${response.header.winveLogin}")
                Log.d("CONTEO_PENDIENTE_LOG", "🔢 [REPOSITORY] Inventario #${response.header.winvdNroInv}")
                Log.d("CONTEO_PENDIENTE_LOG", "📋 [REPOSITORY] Detalles encontrados: ${response.details.size}")
                
                Result.success(response)
                
            } catch (e: Exception) {
                Log.e("CONTEO_PENDIENTE_LOG", "❌ [REPOSITORY] Error al consultar conteos pendientes: ${e.message}")
                Result.failure(e)
            }
        }
    }
}
