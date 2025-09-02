package com.gloria.data.dao

import com.gloria.data.model.ArticuloToma

interface ArticuloTomaDao {
    suspend fun getArticulosToma(nroToma: Int): List<ArticuloToma>
}