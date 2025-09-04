package com.gloria.data.repository

import com.gloria.data.dao.CancelacionTomaDao
import com.gloria.data.model.CancelacionToma
import javax.inject.Inject

/**
 * Repositorio para manejar las operaciones de cancelación de toma
 */
class CancelacionTomaRepository @Inject constructor(
    private val cancelacionTomaDao: CancelacionTomaDao
) {
    
    /**
     * Obtiene las cancelaciones de toma para un usuario
     */
    suspend fun getCancelacionesToma(userLogin: String): List<CancelacionToma> {
        return cancelacionTomaDao.getCancelacionesToma(userLogin)
    }
    
    /**
     * Cancela una toma parcial
     */
    suspend fun cancelarTomaParcial(nroToma: Int, secuencias: List<String>): Int {
        return cancelacionTomaDao.cancelarTomaParcial(nroToma, secuencias)
    }
    
    /**
     * Cancela una toma total
     */
    suspend fun cancelarTomaTotal(nroToma: Int, userLogin: String): Int {
        return cancelacionTomaDao.cancelarTomaTotal(nroToma, userLogin)
    }
}
