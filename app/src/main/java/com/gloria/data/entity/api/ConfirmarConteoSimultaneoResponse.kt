package com.gloria.data.entity.api

import com.google.gson.annotations.SerializedName

data class ConfirmarConteoSimultaneoResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("inventarioNumero")
    val inventarioNumero: Int,
    @SerializedName("articulosProcesados")
    val articulosProcesados: Int,
    @SerializedName("usuariosInvolucrados")
    val usuariosInvolucrados: Int,
    @SerializedName("resultadoOracle")
    val resultadoOracle: ResultadoOracle,
    @SerializedName("registrosPostgreSQLActualizados")
    val registrosPostgreSQLActualizados: Int
)

data class ResultadoOracle(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("totalConteos")
    val totalConteos: Int,
    @SerializedName("conteosOracleInsertados")
    val conteosOracleInsertados: Int,
    @SerializedName("conteosPostgreSQLInsertados")
    val conteosPostgreSQLInsertados: Int,
    @SerializedName("conteosSimulados")
    val conteosSimulados: Int,
    @SerializedName("conteosInsertados")
    val conteosInsertados: List<ConteoInsertado>,
    @SerializedName("message")
    val message: String
)

