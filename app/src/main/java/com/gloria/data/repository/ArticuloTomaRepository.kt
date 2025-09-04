package com.gloria.data.repository

import com.gloria.data.dao.ArticuloTomaDao
import com.gloria.data.model.ArticuloToma
import javax.inject.Inject

/**
 * Repositorio para manejar las operaciones de artículos de toma
 */
class ArticuloTomaRepository @Inject constructor(
    private val articuloTomaDao: ArticuloTomaDao
) {
    
    /**
     * Obtiene los artículos de una toma específica
     */
    suspend fun getArticulosToma(nroToma: Int): List<ArticuloToma> {
        return articuloTomaDao.getArticulosToma(nroToma)
    }
}
