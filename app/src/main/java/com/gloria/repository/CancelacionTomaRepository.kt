package com.gloria.repository

import com.gloria.data.dao.CancelacionTomaDao
import com.gloria.data.model.CancelacionToma
import javax.inject.Inject

class CancelacionTomaRepository @Inject constructor(
    private val cancelacionTomaDao: CancelacionTomaDao
) {
    suspend fun getCancelacionesToma(userLogin: String): List<CancelacionToma> {
        return cancelacionTomaDao.getCancelacionesToma(userLogin)
    }

    suspend fun cancelarTomaParcial(nroToma: Int, secuencias: List<String>): Int {
        return cancelacionTomaDao.cancelarTomaParcial(nroToma, secuencias)
    }

    suspend fun cancelarTomaTotal(nroToma: Int, userLogin: String): Int {
        return cancelacionTomaDao.cancelarTomaTotal(nroToma, userLogin)
    }
}