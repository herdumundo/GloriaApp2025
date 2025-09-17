package com.gloria.data.api

import com.gloria.data.model.ConteoRequest
import com.gloria.data.model.ConteoRequestResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interface para el servicio de envío de conteo de verificación
 * Endpoint: POST /api/stkw002inv/batch?method=jdbc
 */
interface EnviarConteoVerificacionApi {
    
    @POST("api/stkw002inv/batch?method=jdbc")
    suspend fun enviar(@Body request: List<ConteoRequest>): ConteoRequestResponse
}
