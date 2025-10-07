package com.gloria.data.entity.api

data class InsertarCabeceraYDetalleRequest(
    val userdb: String,
    val passdb: String,
    val cabecera: CabeceraInventario,
    val detalle: List<DetalleInventario>
)

data class CabeceraInventario(
    val sucursal: Int,
    val deposito: Int,
    val area: Int,
    val departamento: Int,
    val seccion: Int,
    val idFamilia: String,
    val idGrupo: String,
    val gruposParcial: String,
    val inventarioVisible: Boolean,
    val tipoToma: String,
    val tipoInventario: String  // VALOR "S" SI SERA SIMULTANEO, VALOR "I" SI SERA INDIVIDUAL
)

data class DetalleInventario(
    val artCodigo: String,
    val cantidadActual: Double,
    val lote: String,
    val fechaVencimiento: String,
    val area: Int,
    val departamento: Int,
    val seccion: Int,
    val familia: Int,
    val grupo: Int,
    val subgrupo: Int,
    val consolidado: String
)
