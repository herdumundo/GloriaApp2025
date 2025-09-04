package com.gloria.domain.usecase.toma

import com.gloria.data.entity.Grupo
import com.gloria.data.repository.GrupoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGruposUseCase @Inject constructor(
    private val grupoRepository: GrupoRepository
) {
    suspend operator fun invoke(): Flow<List<Grupo>> {
        return grupoRepository.getAllGrupos()
    }

    suspend fun getGruposByFamilia(familiaCodigo: Int): Flow<List<Grupo>> {
        return grupoRepository.getGruposByFamilia(familiaCodigo)
    }
}
