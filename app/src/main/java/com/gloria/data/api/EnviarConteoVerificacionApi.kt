package com.gloria.data.api

import com.gloria.data.model.ConteoRequest
import com.gloria.data.model.ConteoRequestResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Interface para el servicio de envío de conteo de verificación
 * Endpoint: POST /api/stkw002inv/batch?method=jdbc
 */
interface EnviarConteoVerificacionApi {
    
    /**
     * Envía conteos de verificación al servidor
     * @param request Lista de conteos a enviar
     * @param token Token de autenticación
     * @return Respuesta del servidor
     */
    @POST("api/stkw002inv/upsert-batch?method=jdbc")
    suspend fun enviar(
        @Body request: List<ConteoRequest>,
        @Query("token") token: String
    ): ConteoRequestResponse
}
