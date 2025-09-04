package com.gloria.domain.usecase.toma

import com.gloria.data.entity.Seccion
import com.gloria.data.repository.SeccionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSeccionesUseCase @Inject constructor(
    private val seccionRepository: SeccionRepository
) {
    suspend operator fun invoke(): Flow<List<Seccion>> {
        return seccionRepository.getSeccionesByDepartamento(0) // Default value, should be parameterized
    }

    suspend fun getSeccionesByDepartamento(departamentoCodigo: Int): Flow<List<Seccion>> {
        return seccionRepository.getSeccionesByDepartamento(departamentoCodigo)
    }
}
