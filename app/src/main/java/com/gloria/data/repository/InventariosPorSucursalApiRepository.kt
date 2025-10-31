package com.gloria.data.repository

import com.gloria.BuildConfig
import com.gloria.data.entity.api.InventariosPorSucursalResponse
import com.gloria.data.api.InventariosPorSucursalApiService
import android.util.Log
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
            val response = apiService.getInventariosPorSucursal(
                userdb,
                passdb,
                ardeSuc,
                "Bearer ${BuildConfig.TOKEN_BACKEND}"
            )
            Log.d(
                "InventariosPorSucursalApi",
                "HTTP ${response.code()} - ${response.message()} | isSuccessful=${response.isSuccessful}"
            )
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Log.d(
                    "InventariosPorSucursalApi",
                    "body.success=${body.success} | length=${body.length}"
                )
                if (body.success || body.length > 0 || body.data.isNotEmpty()) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("API Error: Inventarios no disponibles (success=${body.success}, length=${body.length})"))
                }
            } else {
                val errorText = try { response.errorBody()?.string() } catch (e: Exception) { null }
                if (errorText != null) {
                    Log.e("InventariosPorSucursalApi", "errorBody=$errorText")
                }
                Result.failure(Exception("HTTP Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("InventariosPorSucursalApi", "Exception: $e")
            Result.failure(e)
        }
    }
}
