package com.gloria.domain.usecase.toma

import com.gloria.data.repository.LoggedUserRepository
import com.gloria.data.repository.ArticuloLoteRepository
import javax.inject.Inject

class InsertarCabeceraInventarioUseCase @Inject constructor(
    private val loggedUserRepository: LoggedUserRepository
) {
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
        inventarioVisible: Boolean
    ): Int {
        val articuloLoteRepository = ArticuloLoteRepository()
        return articuloLoteRepository.insertarCabeceraInventario(
            sucursal = sucursal,
            deposito = deposito,
            area = area,
            departamento = departamento,
            seccion = seccion,
            familia = familia,
            subgruposSeleccionados = subgruposSeleccionados,
            isFamiliaTodos = isFamiliaTodos,
            userdb = userdb,
            inventarioVisible = inventarioVisible
        )
    }
}
