package com.gloria.data.api

import com.gloria.data.model.ConteosLogPayload
import com.gloria.data.model.ConteosLogsBulkResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ConteosLogsApiService {

    @POST("api/conteos-logs/bulk")
    suspend fun enviarConteosLogs(
        @Header("Authorization") token: String,
        @Body logs: List<ConteosLogPayload>
    ): ConteosLogsBulkResponse
}

