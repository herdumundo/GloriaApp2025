package com.gloria.data.entity.api

import com.google.gson.annotations.SerializedName

/**
 * Request para insertar múltiples conteos usando la nueva API unificada
 * POST /api/oracle/insertar-conteos
 */
data class InsertarConteosRequest(
    @SerializedName("userdb") val userdb: String,
    @SerializedName("passdb") val passdb: String,
    @SerializedName("conteos") val conteos: List<ConteoRequest>
)

/**
 * Request individual de conteo
 */
data class ConteoRequest(
    @SerializedName("cabecera") val cabecera: CabeceraConteo,
    @SerializedName("detalle") val detalle: List<DetalleConteo>
)

/**
 * Cabecera del conteo
 */
data class CabeceraConteo(
    @SerializedName("ardeSuc") val ardeSuc: Int,
    @SerializedName("winveDep") val winveDep: Int,
    @SerializedName("winveLoginCerradoWeb") val winveLoginCerradoWeb: String,
    @SerializedName("usuarioQueConteo") val usuarioQueConteo: String,
    @SerializedName("tipoToma") val tipoToma: String,
    @SerializedName("winvdNroInv") val winvdNroInv: Int,
    @SerializedName("estado") val estado: String
)
/**
 * Detalle del conteo
 */
data class DetalleConteo(
    @SerializedName("winvdArt") val winvdArt: String,
    @SerializedName("winvdSecu") val winvdSecu: Int,
    @SerializedName("winvdCantInv") val winvdCantInv: String,
    @SerializedName("winvdFecVto") val winvdFecVto: String,
    @SerializedName("winvdLote") val winvdLote: String
)