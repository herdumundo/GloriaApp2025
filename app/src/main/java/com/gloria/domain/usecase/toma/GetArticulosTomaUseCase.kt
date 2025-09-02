package com.gloria.domain.usecase.toma

import com.gloria.data.repository.ArticuloLoteRepository
import com.gloria.data.model.ArticuloLote
import javax.inject.Inject

/**
 * Caso de uso para obtener artículos para toma manual
 */
class GetArticulosTomaUseCase @Inject constructor(
    private val articuloLoteRepository: ArticuloLoteRepository
) {
    
    /**
     * Obtiene artículos por criterios de búsqueda
     * @param criterio Criterio de búsqueda (código, descripción, etc.)
     * @param areaId ID del área (opcional)
     * @param departamentoId ID del departamento (opcional)
     * @param seccionId ID de la sección (opcional)
     * @param familiaId ID de la familia (opcional)
     * @param grupoId ID del grupo (opcional)
     * @param subgrupoId ID del subgrupo (opcional)
     */
    suspend operator fun invoke(
        subgruposSeleccionados: List<Pair<Int, Int>> = emptyList(),
        sucursal: Int = 1,
        deposito: Int = 1,
        area: Int = 0,
        departamento: Int = 0,
        seccion: Int = 0,
        familia: String = "",
        isFamiliaTodos: Boolean = false
    ): Result<List<ArticuloLote>> {
        return try {
            // Usar el método existente del repositorio
            val articulosFlow = articuloLoteRepository.getArticulosLotes(
                subgruposSeleccionados = subgruposSeleccionados,
                sucursal = sucursal,
                deposito = deposito,
                area = area,
                departamento = departamento,
                seccion = seccion,
                familia = familia,
                isFamiliaTodos = isFamiliaTodos
            )
            
            // Obtener el primer valor del Flow
            var articulos: List<ArticuloLote> = emptyList()
            articulosFlow.collect { articulos = it }
            Result.success(articulos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
