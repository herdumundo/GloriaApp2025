package com.gloria.data.repository

import com.gloria.BuildConfig
import com.gloria.data.entity.api.OracleLoginResponse
import com.gloria.data.api.OracleLoginApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OracleLoginApiRepository @Inject constructor(
    private val apiService: OracleLoginApiService
) {
    suspend fun oracleLogin(username: String, password: String): Result<OracleLoginResponse> {
        return try {
            val response = apiService.oracleLogin(userdb = username, passdb = password,BuildConfig.TOKEN_BACKEND)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Authentication failed: ${body.message}"))
                }
            } else {
                Result.failure(Exception("HTTP Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
