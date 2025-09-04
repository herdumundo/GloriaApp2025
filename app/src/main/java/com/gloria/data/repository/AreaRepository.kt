package com.gloria.data.repository

import com.gloria.data.dao.AreaDao
import com.gloria.data.entity.Area
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AreaRepository @Inject constructor(
    private val areaDao: AreaDao
) {
    suspend fun getAllAreas(): Flow<List<Area>> {
        return areaDao.getAllAreas()
    }

    suspend fun getAreasBySucursal(sucursalCodigo: Int): Flow<List<Area>> {
        return areaDao.getAreasBySucursal()
    }

    suspend fun getAreasByDepartamento(departamentoCodigo: Int): Flow<List<Area>> {
        return areaDao.getAreasByDepartamento()
    }

    suspend fun insertArea(area: Area) {
        areaDao.insertArea(area)
    }

    suspend fun insertAllAreas(areas: List<Area>) {
        areaDao.insertAllAreas(areas)
    }

    suspend fun deleteAllAreas() {
        areaDao.deleteAllAreas()
    }

    suspend fun getCount(): Int {
        return areaDao.getCount()
    }
}
