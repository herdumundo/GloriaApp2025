package com.gloria.data.entity.api

import com.google.gson.annotations.SerializedName

data class InsertarConteosResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("totalConteos") val totalConteos: Int,
    @SerializedName("totalDetalles") val totalDetalles: Int?,
    @SerializedName("conteosInsertados") val conteosInsertados: List<ConteoInsertado>
)

data class ConteoInsertado(
    @SerializedName("conteoIndex") val conteoIndex: Int,
    @SerializedName("idCabecera") val idCabecera: Int,
    @SerializedName("detallesInsertados") val detallesInsertados: Int,
    @SerializedName("totalDetalles") val totalDetalles: Int,
    @SerializedName("inventarioPadreActualizado") val inventarioPadreActualizado: Int,
    @SerializedName("filasActualizadasPadre") val filasActualizadasPadre: Int
)
