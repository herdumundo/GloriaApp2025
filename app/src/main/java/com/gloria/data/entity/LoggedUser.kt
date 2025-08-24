package com.gloria.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad para almacenar el usuario logueado en la base de datos local
 */
@Entity(tableName = "logged_user")
data class LoggedUser(
    @PrimaryKey
    val id: Int = 1, // Solo un usuario logueado a la vez
    val username: String,
    val password: String,
    val loginTimestamp: Long = System.currentTimeMillis()
)
