package com.gloria.data.api

import com.gloria.data.entity.api.ProductosInventarioPorNumeroResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductosInventarioPorNumeroApiService {
    @GET("api/oracle/productos-inventario-por-numero")
    suspend fun getProductos(
        @Query("userdb") userdb: String,
        @Query("passdb") passdb: String,
        @Query("winveNumero") winveNumero: Int,
        @Query("token") token: String
    ): Response<ProductosInventarioPorNumeroResponse>
}


