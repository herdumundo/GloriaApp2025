package com.gloria.data.entity.api

import com.google.gson.annotations.SerializedName

data class DatosMaestrosResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("areas")
    val areas: List<AreaApi>,
    @SerializedName("departamentos")
    val departamentos: List<DepartamentoApi>,
    @SerializedName("secciones")
    val secciones: List<SeccionApi>,
    @SerializedName("familias")
    val familias: List<FamiliaApi>,
    @SerializedName("grupos")
    val grupos: List<GrupoApi>,
    @SerializedName("subgrupos")
    val subgrupos: List<SubgrupoApi>,
    @SerializedName("sucursales_departamentos")
    val sucursalesDepartamentos: List<SucursalDepartamentoApi>
)

data class AreaApi(
    @SerializedName("AREA_CODIGO")
    val areaCodigo: Int,
    @SerializedName("AREA_DESC")
    val areaDesc: String
)

data class DepartamentoApi(
    @SerializedName("DPTO_CODIGO")
    val dptoCodigo: Int,
    @SerializedName("DPTO_DESC")
    val dptoDesc: String,
    @SerializedName("DPTO_AREA")
    val dptoArea: Int
)

data class SeccionApi(
    @SerializedName("SECC_CODIGO")
    val seccCodigo: Int,
    @SerializedName("SECC_DESC")
    val seccDesc: String,
    @SerializedName("SECC_AREA")
    val seccArea: Int,
    @SerializedName("SECC_DPTO")
    val seccDpto: Int
)

data class FamiliaApi(
    @SerializedName("FLIA_CODIGO")
    val fliaCodigo: Int,
    @SerializedName("FLIA_DESC")
    val fliaDesc: String,
    @SerializedName("FLIA_AREA")
    val fliaArea: Int,
    @SerializedName("FLIA_DPTO")
    val fliaDpto: Int,
    @SerializedName("FLIA_SECCION")
    val fliaSeccion: Int
)

data class GrupoApi(
    @SerializedName("GRUP_CODIGO")
    val grupCodigo: Int,
    @SerializedName("GRUP_DESC")
    val grupDesc: String,
    @SerializedName("GRUP_AREA")
    val grupArea: Int,
    @SerializedName("GRUP_DPTO")
    val grupDpto: Int,
    @SerializedName("GRUP_SECCION")
    val grupSeccion: Int,
    @SerializedName("GRUP_FAMILIA")
    val grupFamilia: Int
)

data class SubgrupoApi(
    @SerializedName("SUGR_CODIGO")
    val sugrCodigo: Int,
    @SerializedName("SUGR_DESC")
    val sugrDesc: String,
    @SerializedName("SUGR_AREA")
    val sugrArea: Int,
    @SerializedName("SUGR_DPTO")
    val sugrDpto: Int,
    @SerializedName("SUGR_SECCION")
    val sugrSeccion: Int,
    @SerializedName("SUGR_FLIA")
    val sugrFlia: Int,
    @SerializedName("SUGR_GRUPO")
    val sugrGrupo: Int
)

data class SucursalDepartamentoApi(
    @SerializedName("SUC_CODIGO")
    val sucCodigo: Int,
    @SerializedName("SUC_DESC")
    val sucDesc: String,
    @SerializedName("DEP_CODIGO")
    val depCodigo: Int,
    @SerializedName("DEP_DESC")
    val depDesc: String,
    @SerializedName("DEP_IND_CUARENTENA")
    val depIndCuarentena: String?,
    @SerializedName("DEP_ESTADO")
    val depEstado: String?,
    @SerializedName("DEP_IND_STOCK")
    val depIndStock: String?,
    @SerializedName("DEP_IND_DEP_PROV")
    val depIndDepProv: String?,
    @SerializedName("DEP_IND_DEP_ANUL")
    val depIndDepAnul: String?,
    @SerializedName("DEP_IND_CAST")
    val depIndCast: String?,
    @SerializedName("DEP_IND_DEP_MKT")
    val depIndDepMkt: String?,
    @SerializedName("DEP_IND_DEP_MKT_SAL")
    val depIndDepMktSal: String?
)
