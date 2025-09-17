package com.gloria.domain.usecase.exportacion

import com.gloria.data.repository.ExportacionConteosRepository
import javax.inject.Inject

/**
 * Caso de uso para exportar conteos realizados
 * Procesa inventarios completados y los marca como cerrados
 */
class ExportarConteosRealizadosUseCase @Inject constructor(
    private val exportacionConteosRepository: ExportacionConteosRepository
) {
    suspend operator fun invoke(
        idSucursal: String,
        userLogin: String
    ): Result<String> {
        return exportacionConteosRepository.exportarConteosRealizados(
            idSucursal = idSucursal,
            userLogin = userLogin
        )
    }
}

/**
 * Caso de uso para exportar conteos para verificación
 * Envía inventarios para revisión sin cambiar su estado
 */
class ExportarConteosParaVerificacionUseCase @Inject constructor(
    private val exportacionConteosRepository: ExportacionConteosRepository
) {
    suspend operator fun invoke(
        idSucursal: String,
        userLogin: String
    ): Result<String> {
        return exportacionConteosRepository.exportarConteosParaVerificacion(
            idSucursal = idSucursal,
            userLogin = userLogin
        )
    }
}
