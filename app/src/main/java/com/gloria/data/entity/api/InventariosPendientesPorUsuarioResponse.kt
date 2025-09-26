package com.gloria.data.entity.api

import com.google.gson.annotations.SerializedName

data class InventariosPendientesPorUsuarioResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<InventarioPendienteApi>,
    @SerializedName("length") val length: Int,
    @SerializedName("usuarioConsultado") val usuarioConsultado: String?
)

data class InventarioPendienteApi(
    @SerializedName("DESC_GRUPOS") val descGrupos: String?,
    @SerializedName("FECHAFORM") val fechaForm: String?,
    @SerializedName("WINVE_NUMERO") val winveNumero: Int,
    @SerializedName("WINVE_EMPR") val winveEmpr: Int?,
    @SerializedName("WINVE_SUC") val winveSuc: Int?,
    @SerializedName("WINVE_DEP") val winveDep: Int?,
    @SerializedName("WINVE_AREA") val winveArea: Int?,
    @SerializedName("WINVE_DPTO") val winveDpto: Int?,
    @SerializedName("WINVE_SECC") val winveSecc: Int?,
    @SerializedName("WINVE_FLIA") val winveFlia: Int?,
    @SerializedName("WINVE_GRUPO") val winveGrupo: Int?,
    @SerializedName("WINVE_GRUPO_PARCIAL") val winveGrupoParcial: String?,
    @SerializedName("WINVE_FEC") val winveFec: String?,
    @SerializedName("WINVE_LOGIN") val winveLogin: String?,
    @SerializedName("WINVE_TIPO_TOMA") val winveTipoToma: String?,
    @SerializedName("WINVE_CONSOLIDADO") val winveConsolidado: String?,
    @SerializedName("WINVE_ESTADO_WEB") val winveEstadoWeb: String?,
    @SerializedName("WINVE_STOCK_VISIBLE") val winveStockVisible: String?,
    @SerializedName("GRUP_CODIGO") val grupCodigo: Int?,
    @SerializedName("GRUP_DESC") val grupDesc: String?,
    @SerializedName("AREA_CODIGO") val areaCodigo: Int?,
    @SerializedName("AREA_DESC") val areaDesc: String?,
    @SerializedName("SECC_CODIGO") val seccCodigo: Int?,
    @SerializedName("SECC_DESC") val seccDesc: String?,
    @SerializedName("DPTO_CODIGO") val dptoCodigo: Int?,
    @SerializedName("DPTO_DESC") val dptoDesc: String?,
    @SerializedName("FLIA_DESC") val fliaDesc: String?,
    @SerializedName("TIPO_TOMA") val tipoToma: String?,
    @SerializedName("CONSOLIDADO") val consolidado: String?,
    @SerializedName("DESC_GRUPO_PARCIAL") val descGrupoParcial: String?
)

fun InventarioPendienteApi.toCancelacionToma(): com.gloria.data.model.CancelacionToma {
    return com.gloria.data.model.CancelacionToma(
        winveNumero = winveNumero,
        fechaForm = fechaForm ?: "",
        tipoToma = tipoToma ?: "",
        consolidado = consolidado ?: "",
        fliaDesc = fliaDesc ?: "",
        grupDesc = grupDesc ?: "",
        sugrDesc = descGrupoParcial ?: "",
        areaDesc = areaDesc ?: "",
        seccDesc = seccDesc ?: "",
        dptoDesc = dptoDesc ?: "",
        winveArea = (winveArea ?: 0).toString(),
        winveSecc = (winveSecc ?: 0).toString(),
        winveDpto = (winveDpto ?: 0).toString(),
        winveFlia = (winveFlia ?: 0).toString(),
        winveGrupo = (winveGrupo ?: 0).toString(),
        winveGrupoParcial = winveGrupoParcial ?: "",
        winveTipoToma = winveTipoToma ?: "",
        winveConsolidado = winveConsolidado ?: "",
        winveEstadoWeb = winveEstadoWeb ?: "",
        winveLogin = winveLogin ?: ""
    )
}


