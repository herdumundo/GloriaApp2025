package com.gloria.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

    @Entity(
        tableName = "departamento",
        primaryKeys = ["dptoCodigo", "dpto_area"],
        indices = [
            androidx.room.Index(value = ["dpto_area"])
        ]
    )
data class Departamento(
    // Clave primaria compuesta - no es auto-incrementable
    val dptoCodigo: Int,
    
    @ColumnInfo(name = "dpto_desc")
    val dptoDesc: String,
    
    @ColumnInfo(name = "dpto_area")
    val dptoArea: Int,
    
    @ColumnInfo(name = "sync_timestamp")
    val syncTimestamp: Long = System.currentTimeMillis()
)
