package com.gloria.data.repository

import com.gloria.data.dao.SucursalDepartamentoDao
import com.gloria.data.entity.Sucursal
import com.gloria.data.entity.SucursalDepartamento
import javax.inject.Inject

class SucursalDepartamentoRepository @Inject constructor(
    private val sucursalDepartamentoDao: SucursalDepartamentoDao
) {
    suspend fun getSucursales(): List<Sucursal> {
        return sucursalDepartamentoDao.getSucursales()
    }

    suspend fun getDepartamentosBySucursal(sucursalCodigo: Int): List<SucursalDepartamento> {
        return sucursalDepartamentoDao.getDepartamentosBySucursal(sucursalCodigo)
    }

    suspend fun insertSucursalDepartamento(sucursalDepartamento: SucursalDepartamento) {
        sucursalDepartamentoDao.insertSucursalDepartamento(sucursalDepartamento)
    }

    suspend fun insertAllSucursalDepartamentos(sucursalDepartamentos: List<SucursalDepartamento>) {
        sucursalDepartamentoDao.insertAllSucursalDepartamentos(sucursalDepartamentos)
    }

    suspend fun deleteAllSucursalDepartamentos() {
        sucursalDepartamentoDao.deleteAllSucursalDepartamentos()
    }

    suspend fun getCount(): Int {
        return sucursalDepartamentoDao.getCount()
    }
}
