package com.gloria.data.model

data class ArticuloToma(
    val winvdSecu: String,
    val winvdArt: String,
    val artDesc: String,
    val winvdLote: String,
    val ardeFecVtoLote: String,
    val fliaDesc: String,
    val grupDesc: String,
    val sugrDesc: String,
    val isSelected: Boolean = false
)