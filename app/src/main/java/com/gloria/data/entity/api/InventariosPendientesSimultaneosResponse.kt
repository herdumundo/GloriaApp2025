package com.gloria.data.entity.api

import com.google.gson.annotations.SerializedName

/**
 * Response para inventarios pendientes simultáneos
 */
data class InventariosPendientesSimultaneosResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: List<InventarioPendienteSimultaneo>
)

/**
 * Modelo para un inventario pendiente simultáneo
 */
data class InventarioPendienteSimultaneo(
    @SerializedName("cabecera")
    val cabecera: CabeceraInventarioPendiente,
    @SerializedName("detalleArticulos")
    val detalleArticulos: List<DetalleArticuloPendiente>,
    @SerializedName("auditoriaUsuarios")
    val auditoriaUsuarios: List<AuditoriaUsuarioPendiente>
)

/**
 * Cabecera del inventario pendiente
 */
data class CabeceraInventarioPendiente(
    @SerializedName("winvdNroInv")
    val winvdNroInv: Int,
    @SerializedName("fliaDesc")
    val fliaDesc: String,
    @SerializedName("grupDesc")
    val grupDesc: String,
    @SerializedName("descGrupoParcial")
    val descGrupoParcial: String,
    @SerializedName("sucursal")
    val sucursal: String,
    @SerializedName("deposito")
    val deposito: String,
    @SerializedName("areaDesc")
    val areaDesc: String,
    @SerializedName("cantidadUsuariosContaron")
    val cantidadUsuariosContaron: Int
)

/**
 * Detalle de artículo pendiente
 */
data class DetalleArticuloPendiente(
    @SerializedName("id")
    val id: Int,
    @SerializedName("winvdNroInv")
    val winvdNroInv: Int,
    @SerializedName("winvdSecu")
    val winvdSecu: Int,
    @SerializedName("winvdArt")
    val winvdArt: String,
    @SerializedName("artDesc")
    val artDesc: String,
    @SerializedName("winvdLote")
    val winvdLote: String,
    @SerializedName("winvdFecVto")
    val winvdFecVto: String,
    @SerializedName("winveFec")
    val winveFec: String?,
    @SerializedName("ardeSuc")
    val ardeSuc: Int,
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
    @SerializedName("estadoConteo")
    val estadoConteo: String,
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
    val unidInd: Int,
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
    @SerializedName("winveLogin")
    val winveLogin: String,
    @SerializedName("tipoToma")
    val tipoToma: String,
    @SerializedName("cantidadTotalContada")
    val cantidadTotalContada: Int,
    @SerializedName("cantidadUsuariosContaron")
    val cantidadUsuariosContaron: Int,
    @SerializedName("fechaCreacion")
    val fechaCreacion: String,
    @SerializedName("fechaActualizacion")
    val fechaActualizacion: String
)

/**
 * Auditoría de usuario pendiente
 */
data class AuditoriaUsuarioPendiente(
    @SerializedName("id")
    val id: Int,
    @SerializedName("winvdNroInv")
    val winvdNroInv: Int,
    @SerializedName("winvdSecu")
    val winvdSecu: Int,
    @SerializedName("winvdArt")
    val winvdArt: String,
    @SerializedName("winvdLote")
    val winvdLote: String,
    @SerializedName("usuarioContador")
    val usuarioContador: String,
    @SerializedName("cantidadContada")
    val cantidadContada: Int,
    @SerializedName("winvdFecVto")
    val winvdFecVto: String,
    @SerializedName("fechaConteo")
    val fechaConteo: String,
    @SerializedName("ardeSuc")
    val ardeSuc: Int,
    @SerializedName("winveDep")
    val winveDep: String,
    @SerializedName("tipoToma")
    val tipoToma: String,
    @SerializedName("dispositivo")
    val dispositivo: String?,
    @SerializedName("observaciones")
    val observaciones: String?
)
