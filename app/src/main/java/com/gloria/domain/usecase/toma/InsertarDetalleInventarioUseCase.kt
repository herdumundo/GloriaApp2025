package com.gloria.domain.usecase.toma

import com.gloria.data.repository.LoggedUserRepository
import com.gloria.data.model.ArticuloLote
import com.gloria.data.repository.ArticuloLoteRepository
import javax.inject.Inject

class InsertarDetalleInventarioUseCase @Inject constructor(
    private val loggedUserRepository: LoggedUserRepository,
    private val articuloLoteRepository: ArticuloLoteRepository
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
        // TODO: Este método necesita ser implementado o refactorizado
        // El ArticuloLoteRepository actual solo maneja consultas API, no inserción de detalles
        throw NotImplementedError("El método insertarDetalleInventario no está implementado en el nuevo ArticuloLoteRepository basado en API")
    }
}
