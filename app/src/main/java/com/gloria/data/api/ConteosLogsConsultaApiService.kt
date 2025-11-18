package com.gloria.data.api

import com.gloria.data.model.ConteosLogPayload
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ConteosLogsConsultaApiService {
    @GET("api/conteos-logs/por-inventario/{nroInventario}")
    suspend fun obtenerConteosPorInventario(
        @Header("Authorization") token: String,
        @Path("nroInventario") numeroInventario: Int
    ): List<ConteosLogPayload>
}

