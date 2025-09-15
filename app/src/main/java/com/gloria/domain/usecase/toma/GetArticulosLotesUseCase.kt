package com.gloria.domain.usecase.toma

import com.gloria.data.repository.LoggedUserRepository
import com.gloria.data.model.ArticuloLote
import com.gloria.data.repository.ArticuloLoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArticulosLotesUseCase @Inject constructor(
    private val articuloLoteRepository: ArticuloLoteRepository
) {
    suspend operator fun invoke(
        subgruposSeleccionados: List<Pair<Int, Int>>,
        sucursal: Int,
        deposito: Int,
        area: Int,
        departamento: Int,
        seccion: Int,
        familia: String,
        isFamiliaTodos: Boolean = false,
        isGruposTodos: Boolean = false, // ✅ Nuevo parámetro para indicar si se seleccionaron todos los grupos
        onProgressUpdate: (current: Int, total: Int) -> Unit
    ): Flow<List<ArticuloLote>> {
        return articuloLoteRepository.getArticulosLotes(
            subgruposSeleccionados = subgruposSeleccionados,
            sucursal = sucursal,
            deposito = deposito,
            area = area,
            departamento = departamento,
            seccion = seccion,
            familia = familia,
            isFamiliaTodos = isFamiliaTodos,
            isGruposTodos = isGruposTodos,
            onProgressUpdate = onProgressUpdate
        )
    }
}
