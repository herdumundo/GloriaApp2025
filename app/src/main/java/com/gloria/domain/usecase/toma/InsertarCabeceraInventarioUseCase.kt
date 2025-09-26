package com.gloria.domain.usecase.toma

import com.gloria.data.repository.LoggedUserRepository
import com.gloria.data.repository.ArticuloLoteRepository
import com.gloria.domain.usecase.AuthSessionUseCase
import javax.inject.Inject

class InsertarCabeceraInventarioUseCase @Inject constructor(
    private val loggedUserRepository: LoggedUserRepository,
    private val articuloLoteRepository: ArticuloLoteRepository
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
        // TODO: Este método necesita ser implementado o refactorizado
        // El ArticuloLoteRepository actual solo maneja consultas API, no inserción de cabeceras
        throw NotImplementedError("El método insertarCabeceraInventario no está implementado en el nuevo ArticuloLoteRepository basado en API")
    }
}
