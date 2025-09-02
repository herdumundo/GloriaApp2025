package com.gloria.data.dao

import com.gloria.data.model.CancelacionToma

interface CancelacionTomaDao {
    suspend fun getCancelacionesToma(userLogin: String): List<CancelacionToma>
    suspend fun cancelarTomaParcial(nroToma: Int, secuencias: List<String>): Int
    suspend fun cancelarTomaTotal(nroToma: Int, userLogin: String): Int
}