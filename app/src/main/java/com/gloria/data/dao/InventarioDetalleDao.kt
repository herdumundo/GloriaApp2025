package com.gloria.data.dao

import androidx.room.*
import com.gloria.data.entity.InventarioDetalle
import com.gloria.data.model.InventarioCard
import com.gloria.data.model.ArticuloInventario
import kotlinx.coroutines.flow.Flow

/**
 * DAO para la entidad InventarioDetalle
 * Proporciona métodos para acceder a la tabla STKW002INV
 */
@Dao
interface InventarioDetalleDao {
    
    /**
     * Obtiene todos los inventarios detalle
     */
    @Query("SELECT * FROM STKW002INV ORDER BY winvd_nro_inv DESC, winvd_secu ASC")
    fun getAllInventariosDetalle(): Flow<List<InventarioDetalle>>
    
    /**
     * Obtiene todos los inventarios detalle de un inventario específico
     */
    @Query("SELECT * FROM STKW002INV WHERE winvd_nro_inv = :numeroInventario ORDER BY winvd_secu ASC")
    fun getInventarioDetalleByNumero(numeroInventario: Int): Flow<List<InventarioDetalle>>
    
    /**
     * Obtiene un inventario detalle específico por número y secuencia
     */
    @Query("SELECT * FROM STKW002INV WHERE winvd_nro_inv = :numeroInventario AND winvd_secu = :secuencia")
    suspend fun getInventarioDetalleByNumeroAndSecuencia(numeroInventario: Int, secuencia: Int): InventarioDetalle?
    
    /**
     * Obtiene el número máximo de inventario
     */
    @Query("SELECT MAX(winvd_nro_inv) FROM STKW002INV")
    suspend fun getMaxNumeroInventario(): Int?
    
    /**
     * Obtiene la secuencia máxima para un inventario específico
     */
    @Query("SELECT MAX(winvd_secu) FROM STKW002INV WHERE winvd_nro_inv = :numeroInventario")
    suspend fun getMaxSecuenciaByInventario(numeroInventario: Int): Int?
    
    /**
     * Obtiene inventarios por usuario
     */
    @Query("SELECT * FROM STKW002INV WHERE winve_login = :usuario ORDER BY winvd_nro_inv DESC, winvd_secu ASC")
    fun getInventariosDetalleByUsuario(usuario: String): Flow<List<InventarioDetalle>>
    
    /**
     * Obtiene inventarios por estado
     */
    @Query("SELECT * FROM STKW002INV WHERE estado = :estado ORDER BY winvd_nro_inv DESC, winvd_secu ASC")
    fun getInventariosDetalleByEstado(estado: String): Flow<List<InventarioDetalle>>
    
    /**
     * Obtiene inventarios por fecha
     */
    @Query("SELECT * FROM STKW002INV WHERE winve_fec = :fecha ORDER BY winvd_nro_inv DESC, winvd_secu ASC")
    fun getInventariosDetalleByFecha(fecha: String): Flow<List<InventarioDetalle>>
    
    /**
     * Obtiene inventarios por familia
     */
    @Query("SELECT * FROM STKW002INV WHERE winvd_flia = :codigoFamilia ORDER BY winvd_nro_inv DESC, winvd_secu ASC")
    fun getInventariosDetalleByFamilia(codigoFamilia: Int): Flow<List<InventarioDetalle>>
    
    /**
     * Obtiene inventarios por grupo
     */
    @Query("SELECT * FROM STKW002INV WHERE winvd_grupo = :codigoGrupo ORDER BY winvd_nro_inv DESC, winvd_secu ASC")
    fun getInventariosDetalleByGrupo(codigoGrupo: Int): Flow<List<InventarioDetalle>>
    
    /**
     * Obtiene inventarios por subgrupo
     */
    @Query("SELECT * FROM STKW002INV WHERE winvd_subgr = :codigoSubgrupo ORDER BY winvd_nro_inv DESC, winvd_secu ASC")
    fun getInventariosDetalleBySubgrupo(codigoSubgrupo: Int): Flow<List<InventarioDetalle>>
    
    /**
     * Obtiene inventarios por área
     */
    @Query("SELECT * FROM STKW002INV WHERE winvd_area = :codigoArea ORDER BY winvd_nro_inv DESC, winvd_secu ASC")
    fun getInventariosDetalleByArea(codigoArea: Int): Flow<List<InventarioDetalle>>
    
    /**
     * Obtiene inventarios por departamento
     */
    @Query("SELECT * FROM STKW002INV WHERE winvd_dpto = :codigoDepartamento ORDER BY winvd_nro_inv DESC, winvd_secu ASC")
    fun getInventariosDetalleByDepartamento(codigoDepartamento: Int): Flow<List<InventarioDetalle>>
    
    /**
     * Obtiene inventarios por sección
     */
    @Query("SELECT * FROM STKW002INV WHERE winvd_secc = :codigoSeccion ORDER BY winvd_nro_inv DESC, winvd_secu ASC")
    fun getInventariosDetalleBySeccion(codigoSeccion: Int): Flow<List<InventarioDetalle>>
    
    /**
     * Obtiene inventarios por sucursal
     */
    @Query("SELECT * FROM STKW002INV WHERE winve_suc = :codigoSucursal ORDER BY winvd_nro_inv DESC, winvd_secu ASC")
    fun getInventariosDetalleBySucursal(codigoSucursal: String): Flow<List<InventarioDetalle>>
    
    /**
     * Obtiene inventarios por depósito
     */
    @Query("SELECT * FROM STKW002INV WHERE winve_dep = :codigoDeposito ORDER BY winvd_nro_inv DESC, winvd_secu ASC")
    fun getInventariosDetalleByDeposito(codigoDeposito: String): Flow<List<InventarioDetalle>>
    
    /**
     * Obtiene inventarios por artículo
     */
    @Query("SELECT * FROM STKW002INV WHERE winvd_art = :codigoArticulo ORDER BY winvd_nro_inv DESC, winvd_secu ASC")
    fun getInventariosDetalleByArticulo(codigoArticulo: String): Flow<List<InventarioDetalle>>
    
    /**
     * Obtiene inventarios por lote
     */
    @Query("SELECT * FROM STKW002INV WHERE winvd_lote = :lote ORDER BY winvd_nro_inv DESC, winvd_secu ASC")
    fun getInventariosDetalleByLote(lote: String): Flow<List<InventarioDetalle>>
    
    /**
     * Obtiene inventarios por rango de fechas
     */
    @Query("SELECT * FROM STKW002INV WHERE winve_fec BETWEEN :fechaInicio AND :fechaFin ORDER BY winvd_nro_inv DESC, winvd_secu ASC")
    fun getInventariosDetalleByRangoFechas(fechaInicio: String, fechaFin: String): Flow<List<InventarioDetalle>>
    
    /**
     * Obtiene inventarios por múltiples criterios
     */
    @Query("""
        SELECT * FROM STKW002INV 
        WHERE (:sucursal IS NULL OR winve_suc = :sucursal)
        AND (:deposito IS NULL OR winve_dep = :deposito)
        AND (:area IS NULL OR winvd_area = :area)
        AND (:departamento IS NULL OR winvd_dpto = :departamento)
        AND (:seccion IS NULL OR winvd_secc = :seccion)
        AND (:familia IS NULL OR winvd_flia = :familia)
        AND (:grupo IS NULL OR winvd_grupo = :grupo)
        AND (:subgrupo IS NULL OR winvd_subgr = :subgrupo)
        AND (:estado IS NULL OR estado = :estado)
        AND (:usuario IS NULL OR winve_login = :usuario)
        ORDER BY winvd_nro_inv DESC, winvd_secu ASC
    """)
    fun getInventariosDetalleByMultiplesCriterios(
        sucursal: String? = null,
        deposito: String? = null,
        area: Int? = null,
        departamento: Int? = null,
        seccion: Int? = null,
        familia: Int? = null,
        grupo: Int? = null,
        subgrupo: Int? = null,
        estado: String? = null,
        usuario: String? = null
    ): Flow<List<InventarioDetalle>>
    
    /**
     * Obtiene inventarios DISTINCT para los cards de registro
     * Basado en el query: SELECT DISTINCT winvd_nro_inv,strftime('%d/%m/%Y %H:%M',winve_fec), area_desc,dpto_desc,tipo_toma,secc_desc,
     * winvd_consolidado,desc_grupo_parcial,desc_familia,sucursal,deposito 
     * FROM STKW002INV WHERE estado = 'A' AND arde_suc = :sucursal ORDER BY 1 DESC
     */
    @Query("""
        SELECT DISTINCT 
            winvd_nro_inv,
            strftime('%d/%m/%Y %H:%M', winve_fec) as fecha_toma,
            area_desc,
            dpto_desc,
            tipo_toma,
            secc_desc,
            winvd_consolidado,
            desc_grupo_parcial,
            desc_familia,
            sucursal,
            deposito,
            estado
        FROM STKW002INV 
        WHERE estado in ('A','P') 
        AND ARDE_SUC = :sucursal
        ORDER BY winvd_nro_inv DESC
    """)
    fun getInventariosCardsDistinct(sucursal: Int): Flow<List<InventarioCard>>
    
    /**
     * Obtiene los artículos de un inventario específico para el conteo físico
     * Basado en el query: SELECT winvd_nro_inv, ART_DESC, winvd_lote, winvd_art, 
     * strftime('%d/%m/%Y',date(winvd_fec_vto)) as winvd_fec_vto, winvd_area, winvd_dpto, 
     * winvd_secc, winvd_flia, winvd_grupo, winvd_cant_act, winvd_cant_inv, winvd_secu, 
     * grup_desc, flia_desc, toma_registro, cod_barra, caja, GRUESA 
     * FROM stkw002inv WHERE winvd_nro_inv = :nroInventario ORDER BY CAST(winvd_art as integer) ASC
     */
    @Query("""
        SELECT 
            winvd_nro_inv,
            art_desc as artDesc,
            winvd_lote as winvdLote,
            winvd_art as winvdArt,
            strftime('%d/%m/%Y', date(winvd_fec_vto)) as winvdFecVto,
            winvd_area as winvdArea,
            winvd_dpto as winvdDpto,
            winvd_secc as winvdSecc,
            winvd_flia as winvdFlia,
            winvd_grupo as winvdGrupo,
            winvd_cant_act as winvdCantAct,
            winvd_cant_inv as winvdCantInv,
            winvd_secu as winvdSecu,
            grup_desc as grupDesc,
            flia_desc as fliaDesc,
            toma_registro as tomaRegistro,
            cod_barra as codBarra,
            caja,
            GRUESA as gruesa,
            stockVisible
        FROM STKW002INV 
        WHERE winvd_nro_inv = :nroInventario 
        ORDER BY CAST(winvd_art as integer) ASC
    """)
    fun getArticulosInventario(nroInventario: Int): Flow<List<ArticuloInventario>>
    
    /**
     * Obtiene el conteo total de inventarios detalle
     */
    @Query("SELECT COUNT(*) FROM STKW002INV")
    suspend fun getTotalInventariosDetalle(): Int
    
    /**
     * Obtiene el conteo de inventarios detalle por inventario
     */
    @Query("SELECT COUNT(*) FROM STKW002INV WHERE winvd_nro_inv = :numeroInventario")
    suspend fun getTotalInventariosDetalleByInventario(numeroInventario: Int): Int
    
    /**
     * Obtiene el conteo de inventarios detalle por usuario
     */
    @Query("SELECT COUNT(*) FROM STKW002INV WHERE winve_login = :usuario")
    suspend fun getTotalInventariosDetalleByUsuario(usuario: String): Int
    
    /**
     * Obtiene el conteo de inventarios detalle por estado
     */
    @Query("SELECT COUNT(*) FROM STKW002INV WHERE estado = :estado")
    suspend fun getTotalInventariosDetalleByEstado(estado: String): Int
    
    /**
     * Obtiene el conteo de inventarios detalle por fecha
     */
    @Query("SELECT COUNT(*) FROM STKW002INV WHERE winve_fec = :fecha")
    suspend fun getTotalInventariosDetalleByFecha(fecha: String): Int
    
    /**
     * Obtiene el conteo de inventarios detalle por familia
     */
    @Query("SELECT COUNT(*) FROM STKW002INV WHERE winvd_flia = :codigoFamilia")
    suspend fun getTotalInventariosDetalleByFamilia(codigoFamilia: Int): Int
    
    /**
     * Obtiene el conteo de inventarios detalle por grupo
     */
    @Query("SELECT COUNT(*) FROM STKW002INV WHERE winvd_grupo = :codigoGrupo")
    suspend fun getTotalInventariosDetalleByGrupo(codigoGrupo: Int): Int
    
    /**
     * Obtiene el conteo de inventarios detalle por subgrupo
     */
    @Query("SELECT COUNT(*) FROM STKW002INV WHERE winvd_subgr = :codigoSubgrupo")
    suspend fun getTotalInventariosDetalleBySubgrupo(codigoSubgrupo: Int): Int
    
    /**
     * Obtiene el conteo de inventarios detalle por área
     */
    @Query("SELECT COUNT(*) FROM STKW002INV WHERE winvd_area = :codigoArea")
    suspend fun getTotalInventariosDetalleByArea(codigoArea: Int): Int
    
    /**
     * Obtiene el conteo de inventarios detalle por departamento
     */
    @Query("SELECT COUNT(*) FROM STKW002INV WHERE winvd_dpto = :codigoDepartamento")
    suspend fun getTotalInventariosDetalleByDepartamento(codigoDepartamento: Int): Int
    
    /**
     * Obtiene el conteo de inventarios detalle por sección
     */
    @Query("SELECT COUNT(*) FROM STKW002INV WHERE winvd_secc = :codigoSeccion")
    suspend fun getTotalInventariosDetalleBySeccion(codigoSeccion: Int): Int
    
    /**
     * Obtiene el conteo de inventarios detalle por sucursal
     */
    @Query("SELECT COUNT(*) FROM STKW002INV WHERE winve_suc = :codigoSucursal")
    suspend fun getTotalInventariosDetalleBySucursal(codigoSucursal: String): Int
    
    /**
     * Obtiene el conteo de inventarios detalle por depósito
     */
    @Query("SELECT COUNT(*) FROM STKW002INV WHERE winve_dep = :codigoDeposito")
    suspend fun getTotalInventariosDetalleByDeposito(codigoDeposito: String): Int
    
    /**
     * Obtiene el conteo de inventarios detalle por artículo
     */
    @Query("SELECT COUNT(*) FROM STKW002INV WHERE winvd_art = :codigoArticulo")
    suspend fun getTotalInventariosDetalleByArticulo(codigoArticulo: String): Int
    
    /**
     * Obtiene el conteo de inventarios detalle por lote
     */
    @Query("SELECT COUNT(*) FROM STKW002INV WHERE winvd_lote = :lote")
    suspend fun getTotalInventariosDetalleByLote(lote: String): Int
    
    /**
     * Obtiene el conteo de inventarios detalle por rango de fechas
     */
    @Query("SELECT COUNT(*) FROM STKW002INV WHERE winve_fec BETWEEN :fechaInicio AND :fechaFin")
    suspend fun getTotalInventariosDetalleByRangoFechas(fechaInicio: String, fechaFin: String): Int
    
    /**
     * Obtiene el conteo de inventarios detalle por múltiples criterios
     */
    @Query("""
        SELECT COUNT(*) FROM STKW002INV 
        WHERE (:sucursal IS NULL OR winve_suc = :sucursal)
        AND (:deposito IS NULL OR winve_dep = :deposito)
        AND (:area IS NULL OR winvd_area = :area)
        AND (:departamento IS NULL OR winvd_dpto = :departamento)
        AND (:seccion IS NULL OR winvd_secc = :seccion)
        AND (:familia IS NULL OR winvd_flia = :familia)
        AND (:grupo IS NULL OR winvd_grupo = :grupo)
        AND (:subgrupo IS NULL OR winvd_subgr = :subgrupo)
        AND (:estado IS NULL OR estado = :estado)
        AND (:usuario IS NULL OR winve_login = :usuario)
    """)
    suspend fun getTotalInventariosDetalleByMultiplesCriterios(
        sucursal: String? = null,
        deposito: String? = null,
        area: Int? = null,
        departamento: Int? = null,
        seccion: Int? = null,
        familia: Int? = null,
        grupo: Int? = null,
        subgrupo: Int? = null,
        estado: String? = null,
        usuario: String? = null
    ): Int
    
    /**
     * Inserta un inventario detalle
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventarioDetalle(inventarioDetalle: InventarioDetalle): Long
    
    /**
     * Inserta múltiples inventarios detalle
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventariosDetalle(inventariosDetalle: List<InventarioDetalle>): List<Long>
    
    /**
     * Actualiza un inventario detalle
     */
    @Update
    suspend fun updateInventarioDetalle(inventarioDetalle: InventarioDetalle)
    
    /**
     * Actualiza múltiples inventarios detalle
     */
    @Update
    suspend fun updateInventariosDetalle(inventariosDetalle: List<InventarioDetalle>)
    
    /**
     * Elimina un inventario detalle
     */
    @Delete
    suspend fun deleteInventarioDetalle(inventarioDetalle: InventarioDetalle)
    
    /**
     * Elimina múltiples inventarios detalle
     */
    @Delete
    suspend fun deleteInventariosDetalle(inventariosDetalle: List<InventarioDetalle>)
    
    /**
     * Elimina un inventario detalle por número y secuencia
     */
    @Query("DELETE FROM STKW002INV WHERE winvd_nro_inv = :numeroInventario AND winvd_secu = :secuencia")
    suspend fun deleteInventarioDetalleByNumeroAndSecuencia(numeroInventario: Int, secuencia: Int)
    
    /**
     * Elimina todos los inventarios detalle de un inventario específico
     */
    @Query("DELETE FROM STKW002INV WHERE winvd_nro_inv = :numeroInventario")
    suspend fun deleteInventariosDetalleByInventario(numeroInventario: Int)
    
    /**
     * Elimina todos los inventarios detalle
     */
    @Query("DELETE FROM STKW002INV")
    suspend fun deleteAllInventariosDetalle()
    
    /**
     * Elimina inventarios detalle por usuario
     */
    @Query("DELETE FROM STKW002INV WHERE winve_login = :usuario")
    suspend fun deleteInventariosDetalleByUsuario(usuario: String)
    
    /**
     * Elimina inventarios detalle por estado
     */
    @Query("DELETE FROM STKW002INV WHERE estado = :estado")
    suspend fun deleteInventariosDetalleByEstado(estado: String)
    
    /**
     * Elimina inventarios detalle por fecha
     */
    @Query("DELETE FROM STKW002INV WHERE winve_fec = :fecha")
    suspend fun deleteInventariosDetalleByFecha(fecha: String)
    
    /**
     * Elimina inventarios detalle por familia
     */
    @Query("DELETE FROM STKW002INV WHERE winvd_flia = :codigoFamilia")
    suspend fun deleteInventariosDetalleByFamilia(codigoFamilia: Int)
    
    /**
     * Elimina inventarios detalle por grupo
     */
    @Query("DELETE FROM STKW002INV WHERE winvd_grupo = :codigoGrupo")
    suspend fun deleteInventariosDetalleByGrupo(codigoGrupo: Int)
    
    /**
     * Elimina inventarios detalle por subgrupo
     */
    @Query("DELETE FROM STKW002INV WHERE winvd_subgr = :codigoSubgrupo")
    suspend fun deleteInventariosDetalleBySubgrupo(codigoSubgrupo: Int)
    
    /**
     * Elimina inventarios detalle por área
     */
    @Query("DELETE FROM STKW002INV WHERE winvd_area = :codigoArea")
    suspend fun deleteInventariosDetalleByArea(codigoArea: Int)
    
    /**
     * Elimina inventarios detalle por departamento
     */
    @Query("DELETE FROM STKW002INV WHERE winvd_dpto = :codigoDepartamento")
    suspend fun deleteInventariosDetalleByDepartamento(codigoDepartamento: Int)
    
    /**
     * Elimina inventarios detalle por sección
     */
    @Query("DELETE FROM STKW002INV WHERE winvd_secc = :codigoSeccion")
    suspend fun deleteInventariosDetalleBySeccion(codigoSeccion: Int)
    
    /**
     * Elimina inventarios detalle por sucursal
     */
    @Query("DELETE FROM STKW002INV WHERE winve_suc = :codigoSucursal")
    suspend fun deleteInventariosDetalleBySucursal(codigoSucursal: String)
    
    /**
     * Elimina inventarios detalle por depósito
     */
    @Query("DELETE FROM STKW002INV WHERE winve_dep = :codigoDeposito")
    suspend fun deleteInventariosDetalleByDeposito(codigoDeposito: String)
    
    /**
     * Elimina inventarios detalle por artículo
     */
    @Query("DELETE FROM STKW002INV WHERE winvd_art = :codigoArticulo")
    suspend fun deleteInventariosDetalleByArticulo(codigoArticulo: String)
    
    /**
     * Elimina inventarios detalle por lote
     */
    @Query("DELETE FROM STKW002INV WHERE winvd_lote = :lote")
    suspend fun deleteInventariosDetalleByLote(lote: String)
    
    /**
     * Elimina inventarios detalle por rango de fechas
     */
    @Query("DELETE FROM STKW002INV WHERE winve_fec BETWEEN :fechaInicio AND :fechaFin")
    suspend fun deleteInventariosDetalleByRangoFechas(fechaInicio: String, fechaFin: String)
    
    /**
     * Elimina inventarios detalle por múltiples criterios
     */
    @Query("""
        DELETE FROM STKW002INV 
        WHERE (:sucursal IS NULL OR winve_suc = :sucursal)
        AND (:deposito IS NULL OR winve_dep = :deposito)
        AND (:area IS NULL OR winvd_dpto = :area)
        AND (:departamento IS NULL OR winvd_dpto = :departamento)
        AND (:seccion IS NULL OR winvd_secc = :seccion)
        AND (:familia IS NULL OR winvd_flia = :familia)
        AND (:grupo IS NULL OR winvd_grupo = :grupo)
        AND (:subgrupo IS NULL OR winvd_subgr = :subgrupo)
        AND (:estado IS NULL OR estado = :estado)
        AND (:usuario IS NULL OR winve_login = :usuario)
    """)
    suspend fun deleteInventariosDetalleByMultiplesCriterios(
        sucursal: String? = null,
        deposito: String? = null,
        area: Int? = null,
        departamento: Int? = null,
        seccion: Int? = null,
        familia: Int? = null,
        grupo: Int? = null,
        subgrupo: Int? = null,
        estado: String? = null,
        usuario: String? = null
    )
    
    /**
     * Actualiza la cantidad inventariada y estado de un artículo específico
     */
    @Query("""
        UPDATE STKW002INV 
        SET winvd_cant_inv = :cantidad,
            estado = :estado
        WHERE winvd_nro_inv = :numeroInventario 
        AND winvd_secu = :secuencia
    """)
    suspend fun actualizarCantidadInventario(
        numeroInventario: Int,
        secuencia: Int,
        cantidad: Int,
        estado: String
    )
    
    /**
     * Actualiza el estado de todos los artículos de un inventario
     */
    @Query("""
        UPDATE STKW002INV 
        SET estado = :estado
        WHERE winvd_nro_inv = :numeroInventario
    """)
    suspend fun actualizarEstadoInventario(
        numeroInventario: Int,
        estado: String
    )
    
    /**
     * Marca un inventario como anulado (estado = 'E')
     */
    @Query("""
        UPDATE STKW002INV 
        SET estado = 'E'
        WHERE winvd_nro_inv = :numeroInventario
    """)
    suspend fun marcarInventarioComoAnulado(numeroInventario: Int)
    
    /**
     * Marca un inventario como cerrado (estado = 'C')
     */
    @Query("""
        UPDATE STKW002INV 
        SET estado = 'C'
        WHERE winvd_nro_inv = :numeroInventario
    """)
    suspend fun marcarInventarioComoCerrado(numeroInventario: Int)
}
