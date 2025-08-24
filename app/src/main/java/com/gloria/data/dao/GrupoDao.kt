package com.gloria.data.dao

import androidx.room.*
import com.gloria.data.entity.Grupo
import kotlinx.coroutines.flow.Flow

@Dao
interface GrupoDao {
    
    @Query("SELECT * FROM grupo ORDER BY grupCodigo")
    fun getAllGrupos(): Flow<List<Grupo>>
    
    @Query("SELECT * FROM grupo WHERE grup_area = :areaCodigo ORDER BY grupCodigo")
    fun getGruposByArea(areaCodigo: Int): Flow<List<Grupo>>
    
    @Query("SELECT * FROM grupo WHERE grup_dpto = :dptoCodigo ORDER BY grupCodigo")
    fun getGruposByDepartamento(dptoCodigo: Int): Flow<List<Grupo>>
    
    @Query("SELECT * FROM grupo WHERE grup_seccion = :seccCodigo ORDER BY grupCodigo")
    fun getGruposBySeccion(seccCodigo: Int): Flow<List<Grupo>>
    
    @Query("SELECT * FROM grupo WHERE grup_familia = :fliaCodigo ORDER BY grupCodigo")
    fun getGruposByFamilia(fliaCodigo: Int): Flow<List<Grupo>>
    
    @Query("SELECT * FROM grupo WHERE grupCodigo = :grupCodigo")
    suspend fun getGrupoByCodigo(grupCodigo: Int): Grupo?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrupo(grupo: Grupo)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllGrupos(grupos: List<Grupo>)
    
    @Update
    suspend fun updateGrupo(grupo: Grupo)
    
    @Delete
    suspend fun deleteGrupo(grupo: Grupo)
    
    @Query("DELETE FROM grupo")
    suspend fun deleteAllGrupos()
    
    @Query("DELETE FROM grupo WHERE grup_area = :areaCodigo")
    suspend fun deleteByArea(areaCodigo: Int)
    
    @Query("DELETE FROM grupo WHERE grup_dpto = :dptoCodigo")
    suspend fun deleteByDepartamento(dptoCodigo: Int)
    
    @Query("DELETE FROM grupo WHERE grup_seccion = :seccCodigo")
    suspend fun deleteBySeccion(seccCodigo: Int)
    
    @Query("DELETE FROM grupo WHERE grup_familia = :fliaCodigo")
    suspend fun deleteByFamilia(fliaCodigo: Int)
    
    @Query("SELECT COUNT(*) FROM grupo")
    suspend fun getCount(): Int
}
