package com.gloria.data.repository

import com.gloria.BuildConfig
import com.gloria.data.entity.api.InventariosPendientesPorUsuarioResponse
import com.gloria.data.api.InventariosPendientesPorUsuarioApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InventariosPendientesPorUsuarioApiRepository @Inject constructor(
    private val apiService: InventariosPendientesPorUsuarioApiService
) {
    suspend fun getInventariosPendientes(userdb: String, passdb: String, usuarioCreador: String): Result<InventariosPendientesPorUsuarioResponse> {
        return try {
            val response = apiService.getInventariosPendientes(userdb, passdb, usuarioCreador,BuildConfig.TOKEN_BACKEND)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) Result.success(body) else Result.failure(Exception("API Error: respuesta success=false"))
            } else {
                Result.failure(Exception("HTTP Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


