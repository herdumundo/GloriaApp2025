package com.gloria.data.api

import com.gloria.data.entity.api.InventariosPendientesSimultaneosResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API Service para obtener inventarios pendientes simultáneos
 */
interface InventariosPendientesSimultaneosApiService {
    
    @GET("api/inventario-simultaneo/inventarios-pendientes")
    suspend fun getInventariosPendientesSimultaneos(
        @Query("userdb") userdb: String,
        @Query("passdb") passdb: String,
        @Query("token") token: String,
        @Query("idSucursal") idSucursal: Int
    ): Response<InventariosPendientesSimultaneosResponse>
}
