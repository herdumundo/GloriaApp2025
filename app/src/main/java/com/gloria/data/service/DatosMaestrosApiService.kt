package com.gloria.data.service

import com.gloria.data.entity.api.DatosMaestrosResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DatosMaestrosApiService {
    @GET("api/oracle/datos-maestros")
    suspend fun getDatosMaestros(
        @Query("userdb") userdb: String,
        @Query("passdb") passdb: String
    ): Response<DatosMaestrosResponse>
}
