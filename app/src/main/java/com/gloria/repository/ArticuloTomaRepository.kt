package com.gloria.repository

import com.gloria.data.dao.ArticuloTomaDao
import com.gloria.data.model.ArticuloToma
import javax.inject.Inject

class ArticuloTomaRepository @Inject constructor(
    private val articuloTomaDao: ArticuloTomaDao
) {
    suspend fun getArticulosToma(nroToma: Int): List<ArticuloToma> {
        return articuloTomaDao.getArticulosToma(nroToma)
    }
}