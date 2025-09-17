package com.gloria.domain.usecase.conteopendiente

import android.util.Log
import com.gloria.data.model.ConteoPendienteResponse
import com.gloria.data.repository.ConteoPendienteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Use case para obtener conteos pendientes por fecha
 */
class GetConteosPendientesByDateUseCase @Inject constructor(
    private val conteoPendienteRepository: ConteoPendienteRepository
) {
    
    /**
     * Obtiene conteos pendientes para una fecha específica
     * @param fecha Fecha en formato Date o String
     * @return Result con la respuesta del endpoint
     */
    suspend operator fun invoke(fecha: Date): Result<ConteoPendienteResponse> {
        return withContext(Dispatchers.Default) {
            try {
                val fechaFormateada = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(fecha)
                Log.d("CONTEO_PENDIENTE_LOG", "🔍 [USE_CASE] Consultando conteos para fecha: $fechaFormateada")
                
                val result = conteoPendienteRepository.getConteosPendientesByDate(fechaFormateada)
                
                if (result.isSuccess) {
                    val response = result.getOrThrow()
                    Log.d("CONTEO_PENDIENTE_LOG", "✅ [USE_CASE] Conteos obtenidos exitosamente")
                    Log.d("CONTEO_PENDIENTE_LOG", "📊 [USE_CASE] Total: ${response.totalRecords} registros")
                    return@withContext Result.success(response)
                } else {
                    Log.e("CONTEO_PENDIENTE_LOG", "❌ [USE_CASE] Error en repository: ${result.exceptionOrNull()?.message}")
                    return@withContext result
                }
                
            } catch (e: Exception) {
                Log.e("CONTEO_PENDIENTE_LOG", "❌ [USE_CASE] Error en use case: ${e.message}")
                return@withContext Result.failure(e)
            }
        }
    }
    
    /**
     * Obtiene conteos pendientes para una fecha en formato string
     * @param fechaString Fecha en formato "yyyy-MM-dd"
     * @return Result con la respuesta del endpoint
     */
    suspend operator fun invoke(fechaString: String): Result<ConteoPendienteResponse> {
        return withContext(Dispatchers.Default) {
            try {
                Log.d("CONTEO_PENDIENTE_LOG", "🔍 [USE_CASE] Consultando conteos para fecha string: $fechaString")
                
                val result = conteoPendienteRepository.getConteosPendientesByDate(fechaString)
                
                if (result.isSuccess) {
                    val response = result.getOrThrow()
                    Log.d("CONTEO_PENDIENTE_LOG", "✅ [USE_CASE] Conteos obtenidos exitosamente")
                    Log.d("CONTEO_PENDIENTE_LOG", "📊 [USE_CASE] Total: ${response.totalRecords} registros")
                    return@withContext Result.success(response)
                } else {
                    Log.e("CONTEO_PENDIENTE_LOG", "❌ [USE_CASE] Error en repository: ${result.exceptionOrNull()?.message}")
                    return@withContext result
                }
                
            } catch (e: Exception) {
                Log.e("CONTEO_PENDIENTE_LOG", "❌ [USE_CASE] Error en use case: ${e.message}")
                return@withContext Result.failure(e)
            }
        }
    }
}
