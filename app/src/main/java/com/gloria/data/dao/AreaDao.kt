package com.gloria.data.dao

import androidx.room.*
import com.gloria.data.entity.Area
import kotlinx.coroutines.flow.Flow

@Dao
interface AreaDao {
    
    @Query("SELECT * FROM area ORDER BY areaCodigo")
    fun getAllAreas(): Flow<List<Area>>
    
    @Query("SELECT * FROM area WHERE areaCodigo = :areaCodigo")
    suspend fun getAreaByCodigo(areaCodigo: Int): Area?
    
    @Query("SELECT * FROM area /*WHERE areaSucursal = :sucursalCodigo*/ ORDER BY areaCodigo")
    fun getAreasBySucursal(): Flow<List<Area>>
    
    @Query("SELECT * FROM area  ORDER BY areaCodigo")
    fun getAreasByDepartamento(): Flow<List<Area>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArea(area: Area)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAreas(areas: List<Area>)
    
    @Update
    suspend fun updateArea(area: Area)
    
    @Delete
    suspend fun deleteArea(area: Area)
    
    @Query("DELETE FROM area")
    suspend fun deleteAllAreas()
    
    @Query("SELECT COUNT(*) FROM area")
    suspend fun getCount(): Int
}
