package com.gloria.domain.usecase.toma

import com.gloria.data.entity.Familia
import com.gloria.data.repository.FamiliaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFamiliasUseCase @Inject constructor(
    private val familiaRepository: FamiliaRepository
) {
    suspend operator fun invoke(): Flow<List<Familia>> {
        return familiaRepository.getFamiliasBySeccion(0) // Default value, should be parameterized
    }

    suspend fun getFamiliasBySeccion(seccionCodigo: Int): Flow<List<Familia>> {
        return familiaRepository.getFamiliasBySeccion(seccionCodigo)
    }
}
