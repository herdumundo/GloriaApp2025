package com.gloria.data.entity.api

import com.google.gson.annotations.SerializedName

data class InventariosPorSucursalResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: List<InventarioSincronizacionApi>,
    @SerializedName("length")
    val length: Int
)

data class InventarioSincronizacionApi(
    @SerializedName("TOMA")
    val toma: String,
    @SerializedName("INVD_CANT_INV")
    val invdCantInv: Int,
    @SerializedName("ART_DESC")
    val artDesc: String,
    @SerializedName("ARDE_SUC")
    val ardeSuc: Int,
    @SerializedName("WINVD_NRO_INV")
    val winvdNroInv: Int,
    @SerializedName("WINVD_ART")
    val winvdArt: Int,
    @SerializedName("WINVD_LOTE")
    val winvdLote: String?,
    @SerializedName("WINVD_FEC_VTO")
    val winvdFecVto: String?,
    @SerializedName("WINVD_AREA")
    val winvdArea: Int,
    @SerializedName("WINVD_DPTO")
    val winvdDpto: Int,
    @SerializedName("WINVD_SECC")
    val winvdSecc: Int,
    @SerializedName("WINVD_FLIA")
    val winvdFlia: Int,
    @SerializedName("WINVD_GRUPO")
    val winvdGrupo: Int,
    @SerializedName("WINVD_CANT_ACT")
    val winvdCantAct: Int,
    @SerializedName("WINVE_FEC")
    val winveFec: String?,
    @SerializedName("DPTO_DESC")
    val dptoDesc: String,
    @SerializedName("SECC_DESC")
    val seccDesc: String,
    @SerializedName("FLIA_DESC")
    val fliaDesc: String,
    @SerializedName("GRUP_DESC")
    val grupDesc: String,
    @SerializedName("AREA_DESC")
    val areaDesc: String,
    @SerializedName("SUGR_CODIGO")
    val sugrCodigo: Int,
    @SerializedName("WINVD_SECU")
    val winvdSecu: Int,
    @SerializedName("TIPO_TOMA")
    val tipoToma: String,
    @SerializedName("WINVE_LOGIN")
    val winveLogin: String,
    @SerializedName("WINVD_CONSOLIDADO")
    val winvdConsolidado: String?,
    @SerializedName("DESC_GRUPO_PARCIAL")
    val descGrupoParcial: String,
    @SerializedName("DESC_FAMILIA")
    val descFamilia: String,
    @SerializedName("WINVE_DEP")
    val winveDep: Int,
    @SerializedName("WINVE_SUC")
    val winveSuc: Int,
    @SerializedName("COBA_CODIGO_BARRA")
    val cobaCodigoBarra: String?,
    @SerializedName("CAJA")
    val caja: Int,
    @SerializedName("GRUESA")
    val gruesa: Double,
    @SerializedName("UNID_IND")
    val unidInd: Int,
    @SerializedName("SUC_DESC")
    val sucDesc: String,
    @SerializedName("DEP_DESC")
    val depDesc: String,
    @SerializedName("WINVE_STOCK_VISIBLE")
    val winveStockVisible: String,
    @SerializedName("WINVE_TIPO")
    val winveTipo: String
)
