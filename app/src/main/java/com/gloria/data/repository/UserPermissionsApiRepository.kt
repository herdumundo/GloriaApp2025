package com.gloria.data.repository

import com.gloria.data.entity.api.UserPermissionsResponse
import com.gloria.data.service.UserPermissionsApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPermissionsApiRepository @Inject constructor(
    private val apiService: UserPermissionsApiService
) {
    suspend fun getUserPermissions(
        username: String, 
        userdb: String = "invap", 
        passdb: String = "invext2024"
    ): Result<UserPermissionsResponse> {
        return try {
            val response = apiService.getUserPermissions(username, userdb, passdb)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("API Error: ${body.message}"))
                }
            } else {
                Result.failure(Exception("HTTP Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
