package com.gloria.domain.usecase.toma

import com.gloria.data.repository.ArticuloLoteRepository
import com.gloria.data.model.ArticuloLote
import javax.inject.Inject

class InsertarCabeceraYDetalleInventarioUseCase @Inject constructor() {
    suspend operator fun invoke(
        sucursal: Int,
        deposito: Int,
        area: Int,
        departamento: Int,
        seccion: Int,
        familia: String?,
        subgruposSeleccionados: List<Pair<Int, Int>>,
        isFamiliaTodos: Boolean,
        userdb: String,
        inventarioVisible: Boolean,
        articulosSeleccionados: List<ArticuloLote>,
        tipoToma: String = "M", // "M" = Manual, "C" = Criterio
        onProgressUpdate: ((current: Int, total: Int) -> Unit)? = null
    ): Pair<Int, Int> {
        val articuloLoteRepository = ArticuloLoteRepository()
        return articuloLoteRepository.insertarCabeceraYDetalleInventario(
            sucursal = sucursal,
            deposito = deposito,
            area = area,
            departamento = departamento,
            seccion = seccion,
            familia = familia,
            subgruposSeleccionados = subgruposSeleccionados,
            isFamiliaTodos = isFamiliaTodos,
            userdb = userdb,
            inventarioVisible = inventarioVisible,
            articulosSeleccionados = articulosSeleccionados,
            tipoToma = tipoToma,
            onProgressUpdate = onProgressUpdate
        )
    }
}
