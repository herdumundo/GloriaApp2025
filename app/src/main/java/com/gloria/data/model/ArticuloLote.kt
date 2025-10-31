package com.gloria.data.model

data class ArticuloLote(
    val concatID: String,
    val cantidad: Double,
    val vencimiento: String,
    val fliaCodigo: String,
    val grupCodigo: Int,
    val grupDesc: String,
    val fliaDesc: String,
    val artDesc: String,
    val ardeLote: String,
    val artCodigo: String,
    val ardeFecVtoLote: String,
    val sugrCodigo: Int,
    val sugrDesc: String,
    val inventarioVisible: String = "N", // Y = Visible, N = No visible
    val winveTipo: String = "I"
)
