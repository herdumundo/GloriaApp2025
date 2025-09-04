package com.gloria.data.repository

import com.gloria.data.dao.GrupoDao
import com.gloria.data.entity.Grupo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GrupoRepository @Inject constructor(
    private val grupoDao: GrupoDao
) {
    suspend fun getAllGrupos(): Flow<List<Grupo>> {
        return grupoDao.getAllGrupos()
    }

    suspend fun getGruposByFamilia(familiaCodigo: Int): Flow<List<Grupo>> {
        return grupoDao.getGruposByFamilia(familiaCodigo)
    }

    suspend fun insertGrupo(grupo: Grupo) {
        grupoDao.insertGrupo(grupo)
    }

    suspend fun insertAllGrupos(grupos: List<Grupo>) {
        grupoDao.insertAllGrupos(grupos)
    }

    suspend fun deleteAllGrupos() {
        grupoDao.deleteAllGrupos()
    }

    suspend fun getCount(): Int {
        return grupoDao.getCount()
    }
}
