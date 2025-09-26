package com.gloria.data.service

import com.gloria.data.entity.api.CancelarTomaRequest
import com.gloria.data.entity.api.CancelarTomaResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CancelarTomaApiService {
    @POST("api/oracle/cancelar-toma")
    suspend fun cancelarToma(
        @Body request: CancelarTomaRequest
    ): Response<CancelarTomaResponse>
}


