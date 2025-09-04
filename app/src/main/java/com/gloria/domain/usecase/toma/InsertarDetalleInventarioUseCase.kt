package com.gloria.domain.usecase.toma

import com.gloria.data.repository.LoggedUserRepository
import com.gloria.data.model.ArticuloLote
import com.gloria.data.repository.ArticuloLoteRepository
import javax.inject.Inject

class InsertarDetalleInventarioUseCase @Inject constructor(
    private val loggedUserRepository: LoggedUserRepository
) {
    suspend operator fun invoke(
        idCabecera: Int,
        articulosSeleccionados: List<ArticuloLote>,
        sucursal: Int,
        deposito: Int,
        area: Int,
        departamento: Int,
        seccion: Int,
        onProgressUpdate: (current: Int, total: Int) -> Unit
    ): Int {
        val articuloLoteRepository = ArticuloLoteRepository()
        return articuloLoteRepository.insertarDetalleInventario(
            idCabecera = idCabecera,
            articulosSeleccionados = articulosSeleccionados,
            sucursal = sucursal,
            deposito = deposito,
            area = area,
            departamento = departamento,
            seccion = seccion,
            onProgressUpdate = onProgressUpdate
        )
    }
}
