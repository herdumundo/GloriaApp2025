package com.gloria.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sucursal_departamento")
data class SucursalDepartamento(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "suc_codigo")
    val sucCodigo: Int,
    
    @ColumnInfo(name = "suc_desc")
    val sucDesc: String,
    
    @ColumnInfo(name = "dep_codigo")
    val depCodigo: Int,
    
    @ColumnInfo(name = "dep_desc")
    val depDesc: String,
    
    @ColumnInfo(name = "sync_timestamp")
    val syncTimestamp: Long = System.currentTimeMillis()
)
