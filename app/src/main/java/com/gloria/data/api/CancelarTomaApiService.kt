package com.gloria.data.api

import com.gloria.data.entity.api.CancelarTomaRequest
import com.gloria.data.entity.api.CancelarTomaResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface CancelarTomaApiService {
    @POST("api/oracle/cancelar-toma")
    suspend fun cancelarToma(
        @Body request: CancelarTomaRequest,
        @Query("token") token: String
    ): Response<CancelarTomaResponse>
}


