package com.gloria.domain.usecase.enviarconteo

import android.util.Log
import com.gloria.data.model.ConteoRequest
import com.gloria.data.model.ConteoRequestResponse
import com.gloria.data.repository.EnviarConteoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Caso de uso para enviar conteos de verificación
 * Maneja la lógica de negocio para el envío de STKW002INV con estado P
 */
class EnviarConteoVerificacionUseCase @Inject constructor(
    private val enviarConteoRepository: EnviarConteoRepository
) {
    
    /**
     * Ejecuta el envío de conteos de verificación
     * @param conteos Lista de conteos a enviar
     * @return Resultado de la operación
     */
    suspend operator fun invoke(conteos: List<ConteoRequest>): Result<ConteoRequestResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("ENVIAR_CONTEO_LOG", "=== INICIANDO EnviarConteoVerificacionUseCase ===")
                Log.d("ENVIAR_CONTEO_LOG", "🔄 Hilo actual: ${Thread.currentThread().name}")
                
                // Validaciones de negocio
                if (conteos.isEmpty()) {
                    Log.w("ENVIAR_CONTEO_LOG", "⚠️ No hay conteos para enviar")
                    return@withContext Result.failure(Exception("No hay conteos para enviar"))
                }
                
                // Filtrar solo conteos con estado P
                val conteosEstadoP = conteos.filter { it.estado == "P" }
                Log.d("ENVIAR_CONTEO_LOG", "📊 Conteos con estado P: ${conteosEstadoP.size} de ${conteos.size}")
                
                if (conteosEstadoP.isEmpty()) {
                    Log.w("ENVIAR_CONTEO_LOG", "⚠️ No hay conteos con estado P para enviar")
                    return@withContext Result.failure(Exception("No hay conteos con estado P para enviar"))
                }
                
                // Enviar al repositorio
                val result = enviarConteoRepository.enviarConteos(conteosEstadoP)
                
                if (result.isSuccess) {
                    Log.d("ENVIAR_CONTEO_LOG", "✅ Envío exitoso")
                    val response = result.getOrThrow()
                    
                    // Validar respuesta del servidor
                    if (!response.success) {
                        Log.w("ENVIAR_CONTEO_LOG", "⚠️ El servidor reportó error: ${response.message}")
                        return@withContext Result.failure(Exception("Error del servidor: ${response.message}"))
                    }
                    
                    Log.d("ENVIAR_CONTEO_LOG", "🎉 Envío completado exitosamente")
                    Log.d("ENVIAR_CONTEO_LOG", "📈 Registros insertados: ${response.registrosInsertados}")
                    Log.d("ENVIAR_CONTEO_LOG", "⏱️ Tiempo de procesamiento: ${response.tiempoMs}ms")
                    
                    return@withContext Result.success(response)
                } else {
                    Log.e("ENVIAR_CONTEO_LOG", "❌ Error en el envío: ${result.exceptionOrNull()?.message}")
                    return@withContext result
                }
                
            } catch (e: Exception) {
                Log.e("ENVIAR_CONTEO_LOG", "❌ Error en EnviarConteoVerificacionUseCase: ${e.message}")
                Log.e("ENVIAR_CONTEO_LOG", "Stack trace: ${e.stackTraceToString()}")
                return@withContext Result.failure(e)
            }
        }
    }
}
