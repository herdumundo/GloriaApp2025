package com.gloria.data.repository

import com.gloria.BuildConfig
import com.gloria.data.entity.api.DatosMaestrosResponse
import com.gloria.data.api.DatosMaestrosApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatosMaestrosApiRepository @Inject constructor(
    private val apiService: DatosMaestrosApiService
) {
    suspend fun getDatosMaestros(userdb: String, passdb: String): Result<DatosMaestrosResponse> {
        return try {
            val response = apiService.getDatosMaestros(userdb, passdb,BuildConfig.TOKEN_BACKEND)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("API Error: Datos maestros no disponibles"))
                }
            } else {
                Result.failure(Exception("HTTP Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
