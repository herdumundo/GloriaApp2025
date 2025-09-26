package com.gloria.data.service

import com.gloria.data.entity.api.InsertarConteosRequest
import com.gloria.data.entity.api.InsertarConteosResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface InsertarConteosApiService {
    @POST("api/oracle/insertar-conteos")
    suspend fun insertarConteos(
        @Body request: InsertarConteosRequest
    ): Response<InsertarConteosResponse>
}
