package com.gloria.data.entity.api

import com.google.gson.annotations.SerializedName

data class UserPermissionsResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: UserPermissionsData?,
    @SerializedName("error")
    val error: String?
)

data class UserPermissionsData(
    @SerializedName("username")
    val username: String,
    @SerializedName("permissions")
    val permissions: List<UserPermissionApi>,
    @SerializedName("totalPermissions")
    val totalPermissions: Int
)

data class UserPermissionApi(
    @SerializedName("formulario")
    val formulario: String,
    @SerializedName("nombre")
    val nombre: String
)
