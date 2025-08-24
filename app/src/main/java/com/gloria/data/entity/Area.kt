package com.gloria.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "area")
data class Area(
    @PrimaryKey
    val areaCodigo: Int,
    
    @ColumnInfo(name = "area_desc")
    val areaDesc: String,
    
    @ColumnInfo(name = "sync_timestamp")
    val syncTimestamp: Long = System.currentTimeMillis()
)
