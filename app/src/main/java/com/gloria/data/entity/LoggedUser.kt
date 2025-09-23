package com.gloria.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "logged_user")
data class LoggedUser(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val password: String,
    val sucursalId: Int? = null,
    val sucursalNombre: String? = null,
    val modoDark: Boolean = false,
    val loginTimestamp: Long
)