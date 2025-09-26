package com.gloria.data.service

import com.gloria.data.entity.api.OracleLoginResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OracleLoginApiService {
    @GET("api/oracle/login")
    suspend fun oracleLogin(
        @Query("userdb") userdb: String,
        @Query("passdb") passdb: String
    ): Response<OracleLoginResponse>
}
