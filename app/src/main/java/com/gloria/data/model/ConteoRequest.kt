package com.gloria.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo para el request de envío de conteo de verificación
 * Basado en el endpoint: POST /api/stkw002inv/batch?method=jdbc
 */
data class ConteoRequest(
    @SerializedName("winvdNroInv") val winvdNroInv: Int,
    @SerializedName("winvdSecu") val winvdSecu: Int,
    @SerializedName("winvdCantAct") val winvdCantAct: Int,
    @SerializedName("winvdCantInv") val winvdCantInv: Int,
    @SerializedName("winvdFecVto") val winvdFecVto: String,
    @SerializedName("winveFec") val winveFec: String,
    @SerializedName("ardeSuc") val ardeSuc: Int,
    @SerializedName("winvdArt") val winvdArt: String,
    @SerializedName("artDesc") val artDesc: String,
    @SerializedName("winvdLote") val winvdLote: String,
    @SerializedName("winvdArea") val winvdArea: Int,
    @SerializedName("areaDesc") val areaDesc: String,
    @SerializedName("winvdDpto") val winvdDpto: Int,
    @SerializedName("dptoDesc") val dptoDesc: String,
    @SerializedName("winvdSecc") val winvdSecc: Int,
    @SerializedName("seccDesc") val seccDesc: String,
    @SerializedName("winvdFlia") val winvdFlia: Int,
    @SerializedName("fliaDesc") val fliaDesc: String,
    @SerializedName("winvdGrupo") val winvdGrupo: Int,
    @SerializedName("grupDesc") val grupDesc: String,
    @SerializedName("winvdSubgr") val winvdSubgr: Int,
    @SerializedName("estado") val estado: String,
    @SerializedName("winveLoginCerradoWeb") val winveLoginCerradoWeb: String,
    @SerializedName("tipoToma") val tipoToma: String,
    @SerializedName("winveLogin") val winveLogin: String,
    @SerializedName("winvdConsolidado") val winvdConsolidado: String,
    @SerializedName("descGrupoParcial") val descGrupoParcial: String,
    @SerializedName("descFamilia") val descFamilia: String,
    @SerializedName("winveDep") val winveDep: String,
    @SerializedName("winveSuc") val winveSuc: String,
    @SerializedName("tomaRegistro") val tomaRegistro: String,
    @SerializedName("codBarra") val codBarra: String,
    @SerializedName("caja") val caja: Int,
    @SerializedName("gruesa") val gruesa: Int,
    @SerializedName("unidInd") val unidInd: Int,
    @SerializedName("sucursal") val sucursal: String,
    @SerializedName("deposito") val deposito: String
)

/**
 * Modelo para la respuesta del endpoint de envío de conteo
 * Basado en la respuesta real del backend
 */
data class ConteoRequestResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("code") val code: Int?,
    @SerializedName("registrosInsertados") val registrosInsertados: Int?,
    @SerializedName("tiempoMs") val tiempoMs: Int?,
    @SerializedName("winvdNroInvList") val winvdNroInvList: List<Int>?,
    @SerializedName("method") val method: String?
)
