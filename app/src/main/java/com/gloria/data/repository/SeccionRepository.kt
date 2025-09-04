package com.gloria.data.repository

import com.gloria.data.dao.SeccionDao
import com.gloria.data.entity.Seccion
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SeccionRepository @Inject constructor(
    private val seccionDao: SeccionDao
) {
    suspend fun getSeccionesByDepartamento(departamentoCodigo: Int): Flow<List<Seccion>> {
        return seccionDao.getSeccionesByDepartamento(departamentoCodigo)
    }

    suspend fun insertSeccion(seccion: Seccion) {
        seccionDao.insertSeccion(seccion)
    }

    suspend fun insertAllSecciones(secciones: List<Seccion>) {
        seccionDao.insertAllSecciones(secciones)
    }

    suspend fun deleteAllSecciones() {
        seccionDao.deleteAllSecciones()
    }

    suspend fun getCount(): Int {
        return seccionDao.getCount()
    }
}
