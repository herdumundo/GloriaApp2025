package com.gloria.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sucursal")
data class Sucursal(
    @PrimaryKey
    @ColumnInfo(name = "suc_codigo")
    val sucCodigo: Int,
    
    @ColumnInfo(name = "suc_desc")
    val sucDesc: String
)
