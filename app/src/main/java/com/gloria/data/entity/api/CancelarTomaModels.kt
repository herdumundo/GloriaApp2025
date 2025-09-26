package com.gloria.data.entity.api

import com.google.gson.annotations.SerializedName

data class CancelarTomaRequest(
    @SerializedName("userdb") val userdb: String,
    @SerializedName("passdb") val passdb: String,
    @SerializedName("winveNumero") val winveNumero: Int,
    @SerializedName("parcial") val parcial: Boolean,
    @SerializedName("usuarioQueCancela") val usuarioQueCancela: String,
    @SerializedName("secuencias") val secuencias: List<Int>
)

data class CancelarTomaResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("winveNumero") val winveNumero: Int,
    @SerializedName("tipoCancelacion") val tipoCancelacion: String,
    @SerializedName("filasAfectadas") val filasAfectadas: Int,
    @SerializedName("detalleOperacion") val detalleOperacion: String?,
    @SerializedName("usuarioQueCancela") val usuarioQueCancela: String?,
    @SerializedName("secuenciasEliminadas") val secuenciasEliminadas: List<Int>
)


