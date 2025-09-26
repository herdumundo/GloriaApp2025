package com.gloria.data.repository

import com.gloria.data.entity.api.InventariosPorSucursalResponse
import com.gloria.data.service.InventariosPorSucursalApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InventariosPorSucursalApiRepository @Inject constructor(
    private val apiService: InventariosPorSucursalApiService
) {
    suspend fun getInventariosPorSucursal(
        userdb: String, 
        passdb: String, 
        ardeSuc: Int
    ): Result<InventariosPorSucursalResponse> {
        return try {
            val response = apiService.getInventariosPorSucursal(userdb, passdb, ardeSuc)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("API Error: Inventarios no disponibles"))
                }
            } else {
                Result.failure(Exception("HTTP Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
