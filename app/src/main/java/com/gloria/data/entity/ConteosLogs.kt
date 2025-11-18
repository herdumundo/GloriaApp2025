package com.gloria.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conteos_logs")
data class ConteosLogs(
    @PrimaryKey
    val id: Long,

    @ColumnInfo(name = "orden")
    val orden : Int,

    @ColumnInfo(name = "winvd_nro_inv")
    val winvd_nro_inv : Int,

    @ColumnInfo(name = "winvd_secu")
    val winvd_secu : Int,

    @ColumnInfo(name = "winvd_art")
    val winvdArt: String,

    @ColumnInfo(name = "winvd_lote")
    val winvdLote: String,

    @ColumnInfo(name = "usuario")
    val usuario: String,

    @ColumnInfo(name = "estado")
    val estado: String = "P",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "cantidad_ingresada")
    val cantidadIngresada: Int,

    @ColumnInfo(name = "cantidad_convertida")
    val cantidadConvertida: Int,

    @ColumnInfo(name = "tipo")
    val tipo: String,

)
