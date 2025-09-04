package com.gloria.data.repository

import com.gloria.data.dao.FamiliaDao
import com.gloria.data.entity.Familia
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FamiliaRepository @Inject constructor(
    private val familiaDao: FamiliaDao
) {
    suspend fun getFamiliasBySeccion(seccionCodigo: Int): Flow<List<Familia>> {
        return familiaDao.getFamiliasBySeccion(seccionCodigo)
    }

    suspend fun insertFamilia(familia: Familia) {
        familiaDao.insertFamilia(familia)
    }

    suspend fun insertAllFamilias(familias: List<Familia>) {
        familiaDao.insertAllFamilias(familias)
    }

    suspend fun deleteAllFamilias() {
        familiaDao.deleteAllFamilias()
    }

    suspend fun getCount(): Int {
        return familiaDao.getCount()
    }
}
