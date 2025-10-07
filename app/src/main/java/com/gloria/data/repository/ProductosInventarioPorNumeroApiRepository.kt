package com.gloria.data.repository

import com.gloria.BuildConfig
import com.gloria.data.entity.api.ProductosInventarioPorNumeroResponse
import com.gloria.data.api.ProductosInventarioPorNumeroApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductosInventarioPorNumeroApiRepository @Inject constructor(
    private val apiService: ProductosInventarioPorNumeroApiService
) {
    suspend fun getProductos(userdb: String, passdb: String, winveNumero: Int): Result<ProductosInventarioPorNumeroResponse> {
        return try {
            val response = apiService.getProductos(userdb, passdb, winveNumero,BuildConfig.TOKEN_BACKEND)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) Result.success(body) else Result.failure(Exception("API Error: success=false"))
            } else {
                Result.failure(Exception("HTTP Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


