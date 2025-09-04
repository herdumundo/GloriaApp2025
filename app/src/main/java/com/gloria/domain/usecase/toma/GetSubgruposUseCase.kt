package com.gloria.domain.usecase.toma

import com.gloria.data.dao.SubgrupoWithGrupo
import com.gloria.data.entity.Subgrupo
import com.gloria.data.repository.SubgrupoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSubgruposUseCase @Inject constructor(
    private val subgrupoRepository: SubgrupoRepository
) {
    suspend operator fun invoke(): Flow<List<Subgrupo>> {
        return subgrupoRepository.getAllSubgrupos()
    }

    suspend fun getAllSubgrupos(): Flow<List<Subgrupo>> {
        return subgrupoRepository.getAllSubgrupos()
    }

    suspend fun getSubgruposCount(): Int {
        return subgrupoRepository.getCount()
    }

    suspend fun testQuerySimple(
        areaCodigo: Int,
        dptoCodigo: Int,
        seccCodigo: Int,
        grupCodigos: List<Int>,
        fliaCodigo: Int
    ): List<Subgrupo> {
        return subgrupoRepository.testQuerySimple(areaCodigo, dptoCodigo, seccCodigo,fliaCodigo,grupCodigos    )
    }

    suspend fun getSubgruposByMultipleGruposWithCompleteJoin(
        gruposCodigos: List<Int>,
        areaCodigo: Int,
        dptoCodigo: Int,
        fliaCodigo: Int,
        seccCodigo: Int,

    ): List<SubgrupoWithGrupo> {
        return subgrupoRepository.getSubgruposByMultipleGruposWithCompleteJoin(gruposCodigos, areaCodigo, dptoCodigo, fliaCodigo,seccCodigo)
    }

    suspend fun getSubgruposByGrupoWithJoin(grupoCodigo: Int, areaCodigo: Int, dptoCodigo: Int, seccCodigo: Int, fliaCodigo: Int): Flow<List<Subgrupo>> {
        return subgrupoRepository.getSubgruposByGrupoWithJoin(grupoCodigo, areaCodigo, dptoCodigo, seccCodigo, fliaCodigo)
    }

    suspend fun getSubgruposByMultipleGruposWithJoin(gruposCodigos: List<Int>, areaCodigo: Int, dptoCodigo: Int, seccCodigo: Int, fliaCodigo: Int): Flow<List<Subgrupo>> {
        return subgrupoRepository.getSubgruposByMultipleGruposWithJoin(gruposCodigos, areaCodigo, dptoCodigo, seccCodigo, fliaCodigo)
    }
}
