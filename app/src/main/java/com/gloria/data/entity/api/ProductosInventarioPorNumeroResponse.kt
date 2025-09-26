package com.gloria.data.entity.api

import com.google.gson.annotations.SerializedName

data class ProductosInventarioPorNumeroResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<ProductoInventarioApi>,
    @SerializedName("length") val length: Int,
    @SerializedName("winveNumero") val winveNumero: Int
)

data class ProductoInventarioApi(
    @SerializedName("WINVD_SECU") val winvdSecu: Int,
    @SerializedName("ARDE_FEC_VTO_LOTE") val ardeFecVtoLote: String?,
    @SerializedName("WINVD_FEC_VTO") val winvdFecVto: String?,
    @SerializedName("ARDE_SUC") val ardeSuc: Int,
    @SerializedName("WINVD_NRO_INV") val winvdNroInv: Int,
    @SerializedName("WINVD_ART") val winvdArt: Int,
    @SerializedName("ART_DESC") val artDesc: String?,
    @SerializedName("WINVD_LOTE") val winvdLote: String?,
    @SerializedName("WINVD_AREA") val winvdArea: Int,
    @SerializedName("WINVD_DPTO") val winvdDpto: Int,
    @SerializedName("WINVD_SECC") val winvdSecc: Int,
    @SerializedName("WINVD_FLIA") val winvdFlia: Int,
    @SerializedName("WINVD_GRUPO") val winvdGrupo: Int,
    @SerializedName("WINVD_CANT_ACT") val winvdCantAct: Int,
    @SerializedName("WINVE_FEC") val winveFec: String?,
    @SerializedName("DPTO_DESC") val dptoDesc: String?,
    @SerializedName("SECC_DESC") val seccDesc: String?,
    @SerializedName("FLIA_DESC") val fliaDesc: String?,
    @SerializedName("GRUP_DESC") val grupDesc: String?,
    @SerializedName("AREA_DESC") val areaDesc: String?,
    @SerializedName("SUGR_DESC") val sugrDesc: String?
)


