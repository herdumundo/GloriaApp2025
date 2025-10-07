package com.gloria.data.repository

import com.gloria.BuildConfig
import com.gloria.data.entity.api.UserPermissionsResponse
import com.gloria.data.api.UserPermissionsApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPermissionsApiRepository @Inject constructor(
    private val apiService: UserPermissionsApiService
) {
    suspend fun getUserPermissions(
        username: String, 
        userdb: String  ,
        passdb: String
    ): Result<UserPermissionsResponse> {
        return try {
            val response = apiService.getUserPermissions(username, userdb, passdb,BuildConfig.TOKEN_BACKEND)
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
