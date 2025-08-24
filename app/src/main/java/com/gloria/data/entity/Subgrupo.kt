package com.gloria.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

    @Entity(
        tableName = "subgrupo",
        primaryKeys = ["sugrCodigo", "sugr_area", "sugr_dpto", "sugr_seccion", "sugr_flia", "sugr_grupo"],
        indices = [
            androidx.room.Index(value = ["sugr_area"]),
            androidx.room.Index(value = ["sugr_dpto"]),
            androidx.room.Index(value = ["sugr_seccion"]),
            androidx.room.Index(value = ["sugr_flia"]),
            androidx.room.Index(value = ["sugr_grupo"])
        ]
    )
data class Subgrupo(
    // Clave primaria compuesta - no es auto-incrementable
    val sugrCodigo: Int,
    
    @ColumnInfo(name = "sugr_desc")
    val sugrDesc: String,
    
    @ColumnInfo(name = "sugr_area")
    val sugrArea: Int,
    
    @ColumnInfo(name = "sugr_dpto")
    val sugrDpto: Int,
    
    @ColumnInfo(name = "sugr_seccion")
    val sugrSeccion: Int,
    
    @ColumnInfo(name = "sugr_flia")
    val sugrFlia: Int,
    
    @ColumnInfo(name = "sugr_grupo")
    val sugrGrupo: Int,
    
    @ColumnInfo(name = "sync_timestamp")
    val syncTimestamp: Long = System.currentTimeMillis()
)
