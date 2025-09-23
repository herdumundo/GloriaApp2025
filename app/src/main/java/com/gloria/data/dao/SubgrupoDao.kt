package com.gloria.data.dao

import androidx.room.*
import com.gloria.data.entity.Subgrupo
import kotlinx.coroutines.flow.Flow
import android.util.Log

// Clase para el resultado del JOIN que incluye la descripción del grupo
data class SubgrupoWithGrupo(
    // Campos del subgrupo con mapeo correcto de columnas
    @ColumnInfo(name = "sugrCodigo")
    val sugrCodigo: Int,
    
    @ColumnInfo(name = "sugr_desc")
    val sugrDesc: String,
    
    @ColumnInfo(name = "sugr_area")
    val sugrArea: Int,
    
    @ColumnInfo(name = "sugr_dpto")
    val sugrDpto: Int,
    
    @ColumnInfo(name = "sugr_seccion")
    val sugrSeccion: Int,
    
    @ColumnInfo(name = "sugr_flia")
    val sugrFlia: Int,
    
    @ColumnInfo(name = "sugr_grupo")
    val sugrGrupo: Int,
    
    @ColumnInfo(name = "sync_timestamp")
    val syncTimestamp: Long,
    
    // Campo adicional del grupo
    @ColumnInfo(name = "grupoDescripcion")
    val grupoDescripcion: String
)

@Dao
interface SubgrupoDao {
    
    @Query("SELECT * FROM subgrupo ORDER BY sugrCodigo")
    fun getAllSubgrupos(): Flow<List<Subgrupo>>
    
    @Query("SELECT * FROM subgrupo WHERE sugr_area = :areaCodigo ORDER BY sugrCodigo")
    fun getSubgruposByArea(areaCodigo: Int): Flow<List<Subgrupo>>
    
    @Query("SELECT * FROM subgrupo WHERE sugr_dpto = :dptoCodigo ORDER BY sugrCodigo")
    fun getSubgruposByDepartamento(dptoCodigo: Int): Flow<List<Subgrupo>>
    
    @Query("SELECT * FROM subgrupo WHERE sugr_seccion = :seccCodigo ORDER BY sugrCodigo")
    fun getSubgruposBySeccion(seccCodigo: Int): Flow<List<Subgrupo>>
    
    @Query("SELECT * FROM subgrupo WHERE sugr_flia = :fliaCodigo ORDER BY sugrCodigo")
    fun getSubgruposByFamilia(fliaCodigo: Int): Flow<List<Subgrupo>>
    
    @Query("SELECT * FROM subgrupo WHERE sugr_grupo = :grupCodigo ORDER BY sugrCodigo")
    fun getSubgruposByGrupo(grupCodigo: Int): Flow<List<Subgrupo>>
    
    // Consulta más específica para obtener subgrupos por grupo con contexto completo
    @Query("""
        SELECT * FROM subgrupo 
        WHERE sugr_grupo = :grupCodigo 
        AND sugr_area = :areaCodigo 
        AND sugr_dpto = :dptoCodigo 
        AND sugr_seccion = :seccCodigo 
        AND sugr_flia = :fliaCodigo
        ORDER BY sugrCodigo
    """)
    fun getSubgruposByGrupoWithContext(
        grupCodigo: Int,
        areaCodigo: Int,
        dptoCodigo: Int,
        seccCodigo: Int,
        fliaCodigo: Int
    ): Flow<List<Subgrupo>>
    
    // Consulta para obtener todos los subgrupos de múltiples grupos
    @Query("""
        SELECT * FROM subgrupo 
        WHERE sugr_grupo IN (:grupCodigos)
        ORDER BY sugrCodigo
    """)
    fun getSubgruposByMultipleGrupos(grupCodigos: List<Int>): Flow<List<Subgrupo>>
    
    // Consulta optimizada usando JOIN completo (equivalente a la consulta Oracle)
    @Query("""
        SELECT s.* FROM subgrupo s
        INNER JOIN grupo g 
        ON s.sugr_grupo = g.grupCodigo 
        AND s.sugr_flia = g.grup_familia 
        AND s.sugr_area = g.grup_area 
        AND s.sugr_seccion = g.grup_seccion 
        AND s.sugr_dpto = g.grup_dpto
        INNER JOIN familia f 
        ON s.sugr_flia = f.fliaCodigo 
        AND s.sugr_area = f.flia_area 
        AND s.sugr_dpto = f.flia_dpto 
        AND s.sugr_seccion = f.flia_seccion
        INNER JOIN seccion sec 
        ON s.sugr_seccion = sec.seccCodigo 
        AND s.sugr_area = sec.secc_area 
        AND s.sugr_dpto = sec.secc_dpto
        INNER JOIN departamento d 
        ON s.sugr_dpto = d.dptoCodigo 
        AND s.sugr_area = d.dpto_area
        INNER JOIN area a 
        ON s.sugr_area = a.areaCodigo
        WHERE s.sugr_area = :areaCodigo 
        AND s.sugr_seccion = :seccCodigo 
        AND s.sugr_dpto = :dptoCodigo
        AND s.sugr_flia = :fliaCodigo
        AND s.sugr_grupo = :grupCodigo
        ORDER BY s.sugrCodigo
    """)
    fun getSubgruposByGrupoWithJoin(
        grupCodigo: Int,
        areaCodigo: Int,
        dptoCodigo: Int,
        seccCodigo: Int,
        fliaCodigo: Int
    ): Flow<List<Subgrupo>>
    
    // Consulta para obtener subgrupos de múltiples grupos usando JOIN completo
    @Query("""
        SELECT s.* FROM subgrupo s
        INNER JOIN grupo g 
        ON s.sugr_grupo = g.grupCodigo 
        AND s.sugr_flia = g.grup_familia 
        AND s.sugr_area = g.grup_area 
        AND s.sugr_seccion = g.grup_seccion 
        AND s.sugr_dpto = g.grup_dpto
        INNER JOIN familia f 
        ON s.sugr_flia = f.fliaCodigo 
        AND s.sugr_area = f.flia_area 
        AND s.sugr_dpto = f.flia_dpto 
        AND s.sugr_seccion = f.flia_seccion
        INNER JOIN seccion sec 
        ON s.sugr_seccion = sec.seccCodigo 
        AND s.sugr_area = sec.secc_area 
        AND s.sugr_dpto = sec.secc_dpto
        INNER JOIN departamento d 
        ON s.sugr_dpto = d.dptoCodigo 
        AND s.sugr_area = d.dpto_area
        INNER JOIN area a 
        ON s.sugr_area = a.areaCodigo
        WHERE s.sugr_area = :areaCodigo 
        AND s.sugr_seccion = :seccCodigo 
        AND s.sugr_dpto = :dptoCodigo
        AND s.sugr_flia = :fliaCodigo
        AND s.sugr_grupo IN (:grupCodigos)
        ORDER BY s.sugrCodigo
    """)
    fun getSubgruposByMultipleGruposWithJoin(
        grupCodigos: List<Int>,
        areaCodigo: Int,
        dptoCodigo: Int,
        seccCodigo: Int,
        fliaCodigo: Int
    ): Flow<List<Subgrupo>>
    
    // Consulta exacta como especificaste (JOIN completo con todas las tablas) + descripción del grupo
    @Query("""
        SELECT s.*, g.grup_desc as grupoDescripcion FROM subgrupo s
        INNER JOIN grupo g 
        ON s.sugr_grupo = g.grupCodigo 
        AND s.sugr_flia = g.grup_familia 
        AND s.sugr_area = g.grup_area 
        AND s.sugr_seccion = g.grup_seccion 
        AND s.sugr_dpto = g.grup_dpto
        INNER JOIN familia f 
        ON s.sugr_flia = f.fliaCodigo 
        AND s.sugr_area = f.flia_area 
        AND s.sugr_dpto = f.flia_dpto 
        AND s.sugr_seccion = f.flia_seccion
        INNER JOIN seccion sec 
        ON s.sugr_seccion = sec.seccCodigo 
        AND s.sugr_area = sec.secc_area 
        AND s.sugr_dpto = sec.secc_dpto
        INNER JOIN departamento d 
        ON s.sugr_dpto = d.dptoCodigo 
        AND s.sugr_area = d.dpto_area
        INNER JOIN area a 
        ON s.sugr_area = a.areaCodigo
        WHERE s.sugr_area = :areaCodigo 
        AND s.sugr_seccion = :seccCodigo 
        AND s.sugr_dpto = :dptoCodigo
        AND s.sugr_flia = :fliaCodigo
        AND s.sugr_grupo IN (:grupCodigos)
        ORDER BY s.sugrCodigo
    """)
    suspend fun getSubgruposByMultipleGruposWithCompleteJoin(
        grupCodigos: List<Int>,
        areaCodigo: Int,
        dptoCodigo: Int,
        fliaCodigo: Int,
        seccCodigo: Int
    ): List<SubgrupoWithGrupo>
    
    @Query("SELECT * FROM subgrupo WHERE sugrCodigo = :sugrCodigo")
    suspend fun getSubgrupoByCodigo(sugrCodigo: Int): Subgrupo?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubgrupo(subgrupo: Subgrupo)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSubgrupos(subgrupos: List<Subgrupo>)
    
    @Update
    suspend fun updateSubgrupo(subgrupo: Subgrupo)
    
    @Delete
    suspend fun deleteSubgrupo(subgrupo: Subgrupo)
    
    @Query("DELETE FROM subgrupo")
    suspend fun deleteAllSubgrupos()
    
    @Query("DELETE FROM subgrupo WHERE sugr_area = :areaCodigo")
    suspend fun deleteByArea(areaCodigo: Int)
    
    @Query("DELETE FROM subgrupo WHERE sugr_dpto = :dptoCodigo")
    suspend fun deleteByDepartamento(dptoCodigo: Int)
    
    @Query("DELETE FROM subgrupo WHERE sugr_seccion = :seccCodigo")
    suspend fun deleteBySeccion(seccCodigo: Int)
    
    @Query("DELETE FROM subgrupo WHERE sugr_flia = :fliaCodigo")
    suspend fun deleteByFamilia(fliaCodigo: Int)
    
    @Query("DELETE FROM subgrupo WHERE sugr_grupo = :grupCodigo")
    suspend fun deleteByGrupo(grupCodigo: Int)
    
    @Query("SELECT COUNT(*) FROM subgrupo")
    suspend fun getCount(): Int
    
    // Función de prueba para verificar la consulta JOIN
    @Query("""
        SELECT s.* FROM subgrupo s
        WHERE s.sugr_area = :areaCodigo 
        AND s.sugr_seccion = :seccCodigo 
        AND s.sugr_dpto = :dptoCodigo
        AND s.sugr_flia = :fliaCodigo
        AND s.sugr_grupo IN (:grupCodigos)
        ORDER BY s.sugrCodigo
    """)
    suspend fun testQuerySimple(
        areaCodigo: Int,
        dptoCodigo: Int,
        seccCodigo: Int,
        fliaCodigo: Int,
        grupCodigos: List<Int>
    ): List<Subgrupo>
}
