package com.gloria.data.entity.api

import com.google.gson.annotations.SerializedName

data class OracleLoginResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("sucursales")
    val sucursales: List<SucursalApi>,
    @SerializedName("permisos")
    val permisos: List<PermisoApi>,
    @SerializedName("message")
    val message: String
)

data class SucursalApi(
    @SerializedName("codigo")
    val codigo: Int,
    @SerializedName("descripcion")
    val descripcion: String
)

data class PermisoApi(
    @SerializedName("formulario")
    val formulario: String,
    @SerializedName("nombre")
    val nombre: String
)
