package com.gloria.domain.usecase.exportacion

import com.gloria.data.repository.ArticuloTomaRepository
import javax.inject.Inject

class GetInventariosPendientesExportarUseCase @Inject constructor(
    private val articuloTomaRepository: ArticuloTomaRepository
) {
    suspend operator fun invoke(
        idSucursal: String,
        userLogin: String
    ): List<InventarioPendienteExportar> {
        return articuloTomaRepository.getInventariosPendientesExportar(
            idSucursal = idSucursal,
            userLogin = userLogin
        )
    }
}

data class InventarioPendienteExportar(
    val winvdNroInv: Int,
    val winveLoginCerradoWeb: String,
    val winveDep: Int,
    val ardeSuc: Int,
    val tipoToma: String,
    val tomaRegistro: String,
    val winveFecha: String,
    val winveSucursal: String
)

