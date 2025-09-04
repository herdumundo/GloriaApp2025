package com.gloria.data.repository

import com.gloria.data.dao.DepartamentoDao
import com.gloria.data.entity.Departamento
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DepartamentoRepository @Inject constructor(
    private val departamentoDao: DepartamentoDao
) {
    suspend fun getDepartamentosByArea(areaCodigo: Int): Flow<List<Departamento>> {
        return departamentoDao.getDepartamentosByArea(areaCodigo)
    }

    suspend fun insertDepartamento(departamento: Departamento) {
        departamentoDao.insertDepartamento(departamento)
    }

    suspend fun insertAllDepartamentos(departamentos: List<Departamento>) {
        departamentoDao.insertAllDepartamentos(departamentos)
    }

    suspend fun deleteAllDepartamentos() {
        departamentoDao.deleteAllDepartamentos()
    }

    suspend fun getCount(): Int {
        return departamentoDao.getCount()
    }
}
