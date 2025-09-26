package com.gloria.data.service

import com.gloria.data.entity.api.UserPermissionsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UserPermissionsApiService {
    @GET("api/auth/user-permissions")
    suspend fun getUserPermissions(
        @Query("username") username: String,
        @Query("userdb") userdb: String,
        @Query("passdb") passdb: String
    ): Response<UserPermissionsResponse>
}
