package com.gloria.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

    @Entity(
        tableName = "familia",
        primaryKeys = ["fliaCodigo", "flia_area", "flia_dpto", "flia_seccion"],
        indices = [
            androidx.room.Index(value = ["flia_area"]),
            androidx.room.Index(value = ["flia_dpto"]),
            androidx.room.Index(value = ["flia_seccion"])
        ]
    )
data class Familia(
    // Clave primaria compuesta - no es auto-incrementable
    val fliaCodigo: Int,
    
    @ColumnInfo(name = "flia_desc")
    val fliaDesc: String,
    
    @ColumnInfo(name = "flia_area")
    val fliaArea: Int,
    
    @ColumnInfo(name = "flia_dpto")
    val fliaDpto: Int,
    
    @ColumnInfo(name = "flia_seccion")
    val fliaSeccion: Int,
    
    @ColumnInfo(name = "sync_timestamp")
    val syncTimestamp: Long = System.currentTimeMillis()
)
