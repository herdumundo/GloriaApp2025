package com.gloria.data.model

import com.google.gson.annotations.SerializedName

data class ConteosLogPayload(
    @SerializedName("id")
    val id: String,
    @SerializedName("orden")
    val orden: Int,
    @SerializedName("winvdNroInv")
    val winvdNroInv: Int,
    @SerializedName("winvdSecu")
    val winvdSecu: Int,
    @SerializedName("winvdArt")
    val winvdArt: String,
    @SerializedName("winvdLote")
    val winvdLote: String,
    @SerializedName("usuario")
    val usuario: String,
    @SerializedName("createdAt")
    val createdAt: Long,
    @SerializedName("cantidadIngresada")
    val cantidadIngresada: Int,
    @SerializedName("cantidadConvertida")
    val cantidadConvertida: Int,
    @SerializedName("tipo")
    val tipo: String
)

