package com.gloria.data.service

import com.gloria.data.entity.api.InventariosPorSucursalResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface InventariosPorSucursalApiService {
    @GET("api/oracle/inventarios-por-sucursal")
    suspend fun getInventariosPorSucursal(
        @Query("userdb") userdb: String,
        @Query("passdb") passdb: String,
        @Query("ardeSuc") ardeSuc: Int
    ): Response<InventariosPorSucursalResponse>
}
