package com.gloria.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

    @Entity(
        tableName = "seccion",
        primaryKeys = ["seccCodigo", "secc_area", "secc_dpto"],
        indices = [
            androidx.room.Index(value = ["secc_area"]),
            androidx.room.Index(value = ["secc_dpto"])
        ]
    )
data class Seccion(
    // Clave primaria compuesta - no es auto-incrementable
    val seccCodigo: Int,
    
    @ColumnInfo(name = "secc_desc")
    val seccDesc: String,
    
    @ColumnInfo(name = "secc_area")
    val seccArea: Int,
    
    @ColumnInfo(name = "secc_dpto")
    val seccDpto: Int,
    
    @ColumnInfo(name = "sync_timestamp")
    val syncTimestamp: Long = System.currentTimeMillis()
)
