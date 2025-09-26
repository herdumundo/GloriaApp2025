package com.gloria.data.entity.api

import com.google.gson.annotations.SerializedName

/**
 * Respuesta de la API para artículos clasificación
 */
data class ArticulosClasificacionResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: List<ArticuloClasificacionApi>,
    @SerializedName("length")
    val length: Int,
    @SerializedName("error")
    val error: String?
)

/**
 * Modelo de artículo clasificación desde la API
 */
data class ArticuloClasificacionApi(
    @SerializedName("ART_CODIGO")
    val artCodigo: Int,
    @SerializedName("ART_DESC")
    val artDesc: String,
    @SerializedName("ART_DESC_ABREV")
    val artDescAbrev: String,
    @SerializedName("ART_COD_ALFANUMERICO")
    val artCodAlfanumerico: String,
    @SerializedName("ART_TIPO")
    val artTipo: Int,
    @SerializedName("ART_IMPU")
    val artImpu: Int,
    @SerializedName("ART_UNID_MED")
    val artUnidMed: String,
    @SerializedName("ART_EST")
    val artEst: String,
    @SerializedName("ART_IND_BLOQUEO")
    val artIndBloqueo: String,
    @SerializedName("ART_CLASIFICACION")
    val artClasificacion: Int,
    @SerializedName("ART_CATEGORIA")
    val artCategoria: Int,
    @SerializedName("STK_COD_EDI")
    val stkCodEdi: String?,
    @SerializedName("ART_IND_LOTE")
    val artIndLote: String,
    @SerializedName("ART_IND_REG_ESPECIAL")
    val artIndRegEspecial: String?,
    @SerializedName("ART_COD_PADRE")
    val artCodPadre: Int?,
    @SerializedName("ART_CODIGO_HIJO")
    val artCodigoHijo: Int?,
    @SerializedName("AREA_CODIGO")
    val areaCodigo: Int,
    @SerializedName("AREA_DESC")
    val areaDesc: String,
    @SerializedName("DPTO_CODIGO")
    val dptoCodigo: Int,
    @SerializedName("DPTO_DESC")
    val dptoDesc: String,
    @SerializedName("SECC_CODIGO")
    val seccCodigo: Int,
    @SerializedName("SECC_DESC")
    val seccDesc: String,
    @SerializedName("FLIA_CODIGO")
    val fliaCodigo: Int,
    @SerializedName("FLIA_DESC")
    val fliaDesc: String,
    @SerializedName("GRUP_CODIGO")
    val grupCodigo: Int,
    @SerializedName("GRUP_DESC")
    val grupDesc: String,
    @SerializedName("SUGR_CODIGO")
    val sugrCodigo: Int,
    @SerializedName("SUGR_DESC")
    val sugrDesc: String,
    @SerializedName("ARDE_SUC")
    val ardeSuc: Int,
    @SerializedName("ARDE_DEP")
    val ardeDep: Int,
    @SerializedName("ARDE_CANT_ACT")
    val ardeCantAct: Double,
    @SerializedName("ARDE_LOTE")
    val ardeLote: String,
    @SerializedName("ARDE_FEC_VTO_LOTE")
    val ardeFecVtoLote: String,
    @SerializedName("COBA_CODIGO_BARRA")
    val cobaCodigoBarra: String,
    @SerializedName("CAJA")
    val caja: Int,
    @SerializedName("GRUESA")
    val gruesa: Double,
    @SerializedName("UNID_IND")
    val unidInd: Int
)
