package com.gloria.domain.usecase.toma

import com.gloria.data.entity.Departamento
import com.gloria.data.repository.DepartamentoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDepartamentosUseCase @Inject constructor(
    private val departamentoRepository: DepartamentoRepository
) {
    suspend operator fun invoke(): Flow<List<Departamento>> {
        return departamentoRepository.getDepartamentosByArea(0) // Default value, should be parameterized
    }

    suspend fun getDepartamentosByArea(areaCodigo: Int): Flow<List<Departamento>> {
        return departamentoRepository.getDepartamentosByArea(areaCodigo)
    }
}
