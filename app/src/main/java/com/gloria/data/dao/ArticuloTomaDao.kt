package com.gloria.data.dao

import com.gloria.data.model.ArticuloToma
import com.gloria.data.repository.DetalleInventarioExportar
import com.gloria.domain.usecase.exportacion.InventarioPendienteExportar

interface ArticuloTomaDao {
    suspend fun getArticulosToma(nroToma: Int): List<ArticuloToma>
    
    /**
     * Obtiene inventarios pendientes de exportar
     * Consulta equivalente a la app antigua:
     * SELECT DISTINCT winvd_nro_inv, WINVE_LOGIN_CERRADO_WEB, winve_dep, arde_suc, tipo_toma, toma_registro 
     * FROM stkw002inv 
     * WHERE arde_suc = ? AND estado IN ('P','F') AND UPPER(WINVE_LOGIN_CERRADO_WEB) = UPPER(?)
     */
    suspend fun getInventariosPendientesExportar(
        idSucursal: String,
        userLogin: String
    ): List<InventarioPendienteExportar>
    
    /**
     * Obtiene los detalles de un inventario específico
     * Consulta equivalente a la app antigua:
     * SELECT winvd_nro_inv, winvd_lote, winvd_art, strftime('%d/%m/%Y',winvd_fec_vto), 
     *        winvd_area, winvd_dpto, winvd_secc, winvd_flia, winvd_grupo, 
     *        winvd_cant_act, winvd_cant_inv, winvd_secu, winve_dep, winve_suc
     * FROM stkw002inv
     * WHERE arde_suc = ? AND estado IN ('P','F') AND winvd_nro_inv = ? AND UPPER(WINVE_LOGIN_CERRADO_WEB) = UPPER(?)
     */
    suspend fun getDetallesInventario(nroInventario: Int): List<DetalleInventarioExportar>
    
    /**
     * Marca un inventario como anulado (estado = 'E')
     */
    suspend fun marcarInventarioComoAnulado(nroInventario: Int)
    
    /**
     * Marca un inventario como cerrado (estado = 'C')
     */
    suspend fun marcarInventarioComoCerrado(nroInventario: Int)
}