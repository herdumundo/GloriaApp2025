package com.gloria.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

    @Entity(
        tableName = "grupo",
        primaryKeys = ["grupCodigo", "grup_area", "grup_dpto", "grup_seccion", "grup_familia"],
        indices = [
            androidx.room.Index(value = ["grup_area"]),
            androidx.room.Index(value = ["grup_dpto"]),
            androidx.room.Index(value = ["grup_seccion"]),
            androidx.room.Index(value = ["grup_familia"])
        ]
    )
data class Grupo(
    // Clave primaria compuesta - no es auto-incrementable
    val grupCodigo: Int,
    
    @ColumnInfo(name = "grup_desc")
    val grupDesc: String,
    
    @ColumnInfo(name = "grup_area")
    val grupArea: Int,
    
    @ColumnInfo(name = "grup_dpto")
    val grupDpto: Int,
    
    @ColumnInfo(name = "grup_seccion")
    val grupSeccion: Int,
    
    @ColumnInfo(name = "grup_familia")
    val grupFamilia: Int,
    
    @ColumnInfo(name = "sync_timestamp")
    val syncTimestamp: Long = System.currentTimeMillis()
)
