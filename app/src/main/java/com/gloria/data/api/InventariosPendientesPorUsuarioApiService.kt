package com.gloria.data.api

import com.gloria.data.entity.api.InventariosPendientesPorUsuarioResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface InventariosPendientesPorUsuarioApiService {
    @GET("api/oracle/inventarios-pendientes-por-usuario")
    suspend fun getInventariosPendientes(
        @Query("userdb") userdb: String,
        @Query("passdb") passdb: String,
        @Query("usuarioCreador") usuarioCreador: String,
        @Query("token") token: String
    ): Response<InventariosPendientesPorUsuarioResponse>
}


