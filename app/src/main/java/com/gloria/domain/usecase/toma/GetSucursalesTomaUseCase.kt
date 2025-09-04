package com.gloria.domain.usecase.toma

import com.gloria.data.dao.SucursalDepartamentoDao
import com.gloria.data.entity.Sucursal
import com.gloria.data.entity.SucursalDepartamento
import com.gloria.data.repository.SucursalDepartamentoRepository
import javax.inject.Inject

class GetSucursalesUseCase @Inject constructor(
    private val sucursalDepartamentoDao: SucursalDepartamentoDao
) {
    suspend operator fun invoke(): List<Sucursal> {
        val repository = SucursalDepartamentoRepository(sucursalDepartamentoDao)
        return repository.getSucursales()
    }

    suspend fun getDepartamentosBySucursal(sucursalCodigo: Int): List<SucursalDepartamento> {
        val repository = SucursalDepartamentoRepository(sucursalDepartamentoDao)
        return repository.getDepartamentosBySucursal(sucursalCodigo)
    }
}
