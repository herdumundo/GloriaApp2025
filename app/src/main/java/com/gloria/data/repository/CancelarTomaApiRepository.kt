package com.gloria.data.repository

import com.gloria.data.entity.api.CancelarTomaRequest
import com.gloria.data.entity.api.CancelarTomaResponse
import com.gloria.data.service.CancelarTomaApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CancelarTomaApiRepository @Inject constructor(
    private val apiService: CancelarTomaApiService
) {
    suspend fun cancelarToma(request: CancelarTomaRequest): Result<CancelarTomaResponse> {
        return try {
            val response = apiService.cancelarToma(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("HTTP Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


