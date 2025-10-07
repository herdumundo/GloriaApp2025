package com.gloria.data.repository

import android.util.Log
import com.gloria.BuildConfig
import com.gloria.data.api.EnviarConteoVerificacionApi
import com.gloria.data.model.ConteoRequest
import com.gloria.data.model.ConteoRequestResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Repositorio para el envío de conteo de verificación
 * Maneja la comunicación con el endpoint de STKW002INV
 */
class EnviarConteoRepository @Inject constructor(
    private val enviarConteoVerificacionApi: EnviarConteoVerificacionApi
) {
    
    /**
     * Envía una lista de conteos de verificación al endpoint
     * @param conteos Lista de conteos a enviar
     * @return Resultado de la operación
     */
    suspend fun enviarConteos(conteos: List<ConteoRequest>): Result<ConteoRequestResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("ENVIAR_CONTEO_LOG", "=== INICIANDO envío de conteos ===")
                Log.d("ENVIAR_CONTEO_LOG", "🔄 Hilo actual: ${Thread.currentThread().name}")
                Log.d("ENVIAR_CONTEO_LOG", "📊 Cantidad de conteos a enviar: ${conteos.size}")
                
                if (conteos.isEmpty()) {
                    Log.w("ENVIAR_CONTEO_LOG", "⚠️ Lista de conteos vacía")
                    return@withContext Result.failure(Exception("No hay conteos para enviar"))
                }
                
                Log.d("ENVIAR_CONTEO_LOG", "🌐 Enviando conteos al endpoint...")
                
                // Token desde BuildConfig
                val token = BuildConfig.TOKEN_BACKEND
                Log.d("ENVIAR_CONTEO_LOG", "🔑 Usando token: ${token.take(20)}...")
                
                val response = enviarConteoVerificacionApi.enviar(conteos, token)
                
                Log.d("ENVIAR_CONTEO_LOG", "✅ Respuesta recibida del servidor")
                Log.d("ENVIAR_CONTEO_LOG", "📈 Success: ${response.success}")
                Log.d("ENVIAR_CONTEO_LOG", "💬 Message: ${response.message}")
                Log.d("ENVIAR_CONTEO_LOG", "🔢 Code: ${response.code}")
                Log.d("ENVIAR_CONTEO_LOG", "📊 Registros Insertados: ${response.registrosInsertados}")
                Log.d("ENVIAR_CONTEO_LOG", "⏱️ Tiempo (ms): ${response.tiempoMs}")
                Log.d("ENVIAR_CONTEO_LOG", "🔧 Method: ${response.method}")
                
                if (response.winvdNroInvList?.isNotEmpty() == true) {
                    Log.d("ENVIAR_CONTEO_LOG", "📋 Inventarios procesados: ${response.winvdNroInvList}")
                }
                
                Result.success(response)
                
            } catch (e: Exception) {
                Log.e("ENVIAR_CONTEO_LOG", "❌ Error al enviar conteos: ${e.message}")
                Log.e("ENVIAR_CONTEO_LOG", "Stack trace: ${e.stackTraceToString()}")
                Result.failure(e)
            }
        }
    }
}
