package com.gloria.data.repository

import com.gloria.data.dao.SubgrupoDao
import com.gloria.data.dao.SubgrupoWithGrupo
import com.gloria.data.entity.Subgrupo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubgrupoRepository @Inject constructor(
    private val subgrupoDao: SubgrupoDao
) {
    suspend fun getAllSubgrupos(): Flow<List<Subgrupo>> {
        return subgrupoDao.getAllSubgrupos()
    }

    suspend fun getSubgruposByGrupoWithJoin(grupoCodigo: Int, areaCodigo: Int, dptoCodigo: Int, seccCodigo: Int, fliaCodigo: Int): Flow<List<Subgrupo>> {
        return subgrupoDao.getSubgruposByGrupoWithJoin(grupoCodigo, areaCodigo, dptoCodigo, seccCodigo, fliaCodigo)
    }

    suspend fun getSubgruposByMultipleGruposWithJoin(gruposCodigos: List<Int>, areaCodigo: Int, dptoCodigo: Int, seccCodigo: Int, fliaCodigo: Int): Flow<List<Subgrupo>> {
        return subgrupoDao.getSubgruposByMultipleGruposWithJoin(gruposCodigos, areaCodigo, dptoCodigo, seccCodigo, fliaCodigo)
    }

    suspend fun getSubgruposByMultipleGruposWithCompleteJoin(gruposCodigos: List<Int>, areaCodigo: Int, dptoCodigo: Int, fliaCodigo: Int,seccCodigo: Int): List<SubgrupoWithGrupo> {
        return subgrupoDao.getSubgruposByMultipleGruposWithCompleteJoin(gruposCodigos, areaCodigo, dptoCodigo, fliaCodigo,seccCodigo)
    }

    suspend fun testQuerySimple( areaCodigo: Int,
                                 dptoCodigo: Int,
                                 seccCodigo: Int,
                                 fliaCodigo: Int,
                                 gruposCodigos: List<Int>,): List<Subgrupo> {
        return subgrupoDao.testQuerySimple(areaCodigo, dptoCodigo, seccCodigo,fliaCodigo,gruposCodigos)
    }

    suspend fun insertSubgrupo(subgrupo: Subgrupo) {
        subgrupoDao.insertSubgrupo(subgrupo)
    }

    suspend fun insertAllSubgrupos(subgrupos: List<Subgrupo>) {
        subgrupoDao.insertAllSubgrupos(subgrupos)
    }

    suspend fun deleteAllSubgrupos() {
        subgrupoDao.deleteAllSubgrupos()
    }

    suspend fun getCount(): Int {
        return subgrupoDao.getCount()
    }
}
