package com.gloria.data.model

import com.google.gson.annotations.SerializedName

/**
 * Respuesta del endpoint /api/stkw002inv/by-date-grouped-by-inventory/{date}
 */
data class ConteoPendienteResponse(
    @SerializedName("inventories")
    val inventories: List<InventarioConteo>,
    @SerializedName("totalInventories")
    val totalInventories: Int,
    @SerializedName("totalRecords")
    val totalRecords: Int,
    @SerializedName("date")
    val date: String
)

/**
 * Inventario individual con su cabecera y detalles
 */
data class InventarioConteo(
    @SerializedName("header")
    val header: InventarioHeader,
    @SerializedName("details")
    val details: List<InventarioDetail>,
    @SerializedName("totalRecords")
    val totalRecords: Int,
    @SerializedName("date")
    val date: String
)

/**
 * Cabecera del inventario
 */
data class InventarioHeader(
    @SerializedName("winvdNroInv")
    val winvdNroInv: Int,
    @SerializedName("winveLogin")
    val winveLogin: String,
    @SerializedName("descGrupoParcial")
    val descGrupoParcial: String,
    @SerializedName("sucursal")
    val sucursal: String,
    @SerializedName("deposito")
    val deposito: String,
    @SerializedName("areaDesc")
    val areaDesc: String,
    @SerializedName("dptoDesc")
    val dptoDesc: String,
    @SerializedName("seccDesc")
    val seccDesc: String,
    @SerializedName("tipoToma")
    val tipoToma: String
)

/**
 * Detalle del inventario
 */
data class InventarioDetail(
    @SerializedName("id")
    val id: Int,
    @SerializedName("winvdSecu")
    val winvdSecu: Int,
    @SerializedName("winvdCantAct")
    val winvdCantAct: Int,
    @SerializedName("winvdCantInv")
    val winvdCantInv: Int,
    @SerializedName("winvdFecVto")
    val winvdFecVto: String,
    @SerializedName("winveFec")
    val winveFec: String,
    @SerializedName("ardeSuc")
    val ardeSuc: Int,
    @SerializedName("winvdArt")
    val winvdArt: String,
    @SerializedName("artDesc")
    val artDesc: String,
    @SerializedName("winvdLote")
    val winvdLote: String,
    @SerializedName("winvdArea")
    val winvdArea: Int,
    @SerializedName("winvdDpto")
    val winvdDpto: Int,
    @SerializedName("winvdSecc")
    val winvdSecc: Int,
    @SerializedName("winvdFlia")
    val winvdFlia: Int,
    @SerializedName("fliaDesc")
    val fliaDesc: String,
    @SerializedName("winvdGrupo")
    val winvdGrupo: Int,
    @SerializedName("grupDesc")
    val grupDesc: String,
    @SerializedName("winvdSubgr")
    val winvdSubgr: Int,
    @SerializedName("estado")
    val estado: String,
    @SerializedName("winveLoginCerradoWeb")
    val winveLoginCerradoWeb: String,
    @SerializedName("winvdConsolidado")
    val winvdConsolidado: String,
    @SerializedName("descFamilia")
    val descFamilia: String,
    @SerializedName("winveDep")
    val winveDep: String,
    @SerializedName("winveSuc")
    val winveSuc: String,
    @SerializedName("tomaRegistro")
    val tomaRegistro: String,
    @SerializedName("codBarra")
    val codBarra: String,
    @SerializedName("caja")
    val caja: Int,
    @SerializedName("gruesa")
    val gruesa: Int,
    @SerializedName("unidInd")
    val unidInd: Int
)