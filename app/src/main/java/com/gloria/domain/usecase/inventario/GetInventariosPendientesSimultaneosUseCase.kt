package com.gloria.domain.usecase.inventario

import com.gloria.data.entity.api.InventarioPendienteSimultaneo
import com.gloria.data.repository.InventariosPendientesSimultaneosRepository
import javax.inject.Inject

/**
 * Caso de uso para obtener inventarios pendientes simultáneos
 */
class GetInventariosPendientesSimultaneosUseCase @Inject constructor(
    private val repository: InventariosPendientesSimultaneosRepository
) {
    
    /**
     * Ejecuta el caso de uso para obtener inventarios pendientes simultáneos
     */
    suspend operator fun invoke(): Result<List<InventarioPendienteSimultaneo>> {
        return repository.getInventariosPendientesSimultaneos()
    }
}
