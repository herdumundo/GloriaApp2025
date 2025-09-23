package com.gloria.domain.usecase.toma

import com.gloria.data.repository.LoggedUserRepository
import com.gloria.data.model.ArticuloLote
import com.gloria.data.repository.ArticuloLoteRepository
import com.gloria.domain.usecase.AuthSessionUseCase
import javax.inject.Inject

class InsertarDetalleInventarioUseCase @Inject constructor(
    private val loggedUserRepository: LoggedUserRepository,
    private val authSessionUseCase: AuthSessionUseCase
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
        val articuloLoteRepository = ArticuloLoteRepository(authSessionUseCase)
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
