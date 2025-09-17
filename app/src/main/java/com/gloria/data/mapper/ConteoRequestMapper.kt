package com.gloria.data.mapper

import com.gloria.data.entity.InventarioDetalle
import com.gloria.data.model.ConteoRequest
import com.gloria.data.repository.DetalleInventarioExportar

/**
 * Mapper para convertir datos de SQLite a ConteoRequest
 * Este mapper ayuda a transformar los datos locales en el formato requerido por el endpoint
 */
object ConteoRequestMapper {

    /**
     * Convierte una lista de InventarioDetalle a ConteoRequest
     * @param inventarios Lista de inventarios desde SQLite
     * @return Lista de ConteoRequest para enviar al endpoint
     */
    fun toConteoRequestList(inventarios: List<InventarioDetalle>): List<ConteoRequest> {
        return inventarios.map { inventario ->
            ConteoRequest(
                winvdNroInv = inventario.winvd_nro_inv,
                winvdSecu = inventario.winvd_secu,
                winvdCantAct = inventario.winvd_cant_act,
                winvdCantInv = inventario.winvd_cant_inv,
                winvdFecVto = inventario.winvd_fec_vto,
                winveFec = inventario.winve_fec,
                ardeSuc = inventario.ARDE_SUC,
                winvdArt = inventario.winvd_art,
                artDesc = inventario.art_desc,
                winvdLote = inventario.winvd_lote,
                winvdArea = inventario.winvd_area,
                areaDesc = inventario.area_desc,
                winvdDpto = inventario.winvd_dpto,
                dptoDesc = inventario.dpto_desc,
                winvdSecc = inventario.winvd_secc,
                seccDesc = inventario.secc_desc,
                winvdFlia = inventario.winvd_flia,
                fliaDesc = inventario.flia_desc,
                winvdGrupo = inventario.winvd_grupo,
                grupDesc = inventario.grup_desc,
                winvdSubgr = inventario.winvd_subgr,
                estado = inventario.estado,
                winveLoginCerradoWeb = inventario.WINVE_LOGIN_CERRADO_WEB,
                tipoToma = inventario.tipo_toma,
                winveLogin = inventario.winve_login,
                winvdConsolidado = inventario.winvd_consolidado,
                descGrupoParcial = inventario.desc_grupo_parcial,
                descFamilia = inventario.desc_familia,
                winveDep = inventario.winve_dep,
                winveSuc = inventario.winve_suc,
                tomaRegistro = inventario.toma_registro,
                codBarra = inventario.cod_barra,
                caja = inventario.caja,
                gruesa = inventario.GRUESA,
                unidInd = inventario.UNID_IND,
                sucursal = inventario.sucursal,
                deposito = inventario.deposito
            )
        }
    }

    /**
     * Convierte un solo InventarioDetalle a ConteoRequest
     * @param inventario Inventario desde SQLite
     * @return ConteoRequest para enviar al endpoint
     */
    fun toConteoRequest(inventario: InventarioDetalle): ConteoRequest {
        return ConteoRequest(
            winvdNroInv = inventario.winvd_nro_inv,
            winvdSecu = inventario.winvd_secu,
            winvdCantAct = inventario.winvd_cant_act,
            winvdCantInv = inventario.winvd_cant_inv,
            winvdFecVto = inventario.winvd_fec_vto,
            winveFec = inventario.winve_fec,
            ardeSuc = inventario.ARDE_SUC,
            winvdArt = inventario.winvd_art,
            artDesc = inventario.art_desc,
            winvdLote = inventario.winvd_lote,
            winvdArea = inventario.winvd_area,
            areaDesc = inventario.area_desc,
            winvdDpto = inventario.winvd_dpto,
            dptoDesc = inventario.dpto_desc,
            winvdSecc = inventario.winvd_secc,
            seccDesc = inventario.secc_desc,
            winvdFlia = inventario.winvd_flia,
            fliaDesc = inventario.flia_desc,
            winvdGrupo = inventario.winvd_grupo,
            grupDesc = inventario.grup_desc,
            winvdSubgr = inventario.winvd_subgr,
            estado = inventario.estado,
            winveLoginCerradoWeb = inventario.WINVE_LOGIN_CERRADO_WEB,
            tipoToma = inventario.tipo_toma,
            winveLogin = inventario.winve_login,
            winvdConsolidado = inventario.winvd_consolidado,
            descGrupoParcial = inventario.desc_grupo_parcial,
            descFamilia = inventario.desc_familia,
            winveDep = inventario.winve_dep,
            winveSuc = inventario.winve_suc,
            tomaRegistro = inventario.toma_registro,
            codBarra = inventario.cod_barra,
            caja = inventario.caja,
            gruesa = inventario.GRUESA,
            unidInd = inventario.UNID_IND,
            sucursal = inventario.sucursal,
            deposito = inventario.deposito
        )
    }

    /**
     * Convierte una lista de DetalleInventarioExportar a ConteoRequest
     * @param detalles Lista de detalles desde Oracle
     * @return Lista de ConteoRequest para enviar al endpoint
     */
    fun toConteoRequestListFromDetalleExportar(detalles: List<DetalleInventarioExportar>): List<ConteoRequest> {
        return detalles.map { detalle ->
            ConteoRequest(
                winvdNroInv = detalle.winvdNroInv,
                winvdSecu = detalle.winvdSecu,
                winvdCantAct = detalle.winvdCantAct.toIntOrNull() ?: 0,
                winvdCantInv = detalle.winvdCantInv.toIntOrNull() ?: 0,
                winvdFecVto = detalle.winvdFecVto,
                winveFec = detalle.winveFec,
                ardeSuc = detalle.winveSuc,
                winvdArt = detalle.winvdArt,
                artDesc = detalle.artDesc,
                winvdLote = detalle.winvdLote,
                winvdArea = detalle.winvdArea.toIntOrNull() ?: 0,
                areaDesc = detalle.areaDesc,
                winvdDpto = detalle.winvdDpto.toIntOrNull() ?: 0,
                dptoDesc = detalle.dptoDesc,
                winvdSecc = detalle.winvdSecc.toIntOrNull() ?: 0,
                seccDesc = detalle.seccDesc,
                winvdFlia = detalle.winvdFlia.toIntOrNull() ?: 0,
                fliaDesc = detalle.fliaDesc,
                winvdGrupo = detalle.winvdGrupo.toIntOrNull() ?: 0,
                grupDesc = detalle.grupDesc,
                winvdSubgr = detalle.winvdSubgr,
                estado = detalle.estado,
                winveLoginCerradoWeb = detalle.winveLoginCerradoWeb,
                tipoToma = detalle.tipoToma,
                winveLogin = detalle.winveLogin,
                winvdConsolidado = detalle.winvdConsolidado,
                descGrupoParcial = detalle.descGrupoParcial,
                descFamilia = detalle.descFamilia,
                winveDep = detalle.winveDep.toString(),
                winveSuc = detalle.winveSuc.toString(),
                tomaRegistro = detalle.tomaRegistro,
                codBarra = detalle.codBarra,
                caja = detalle.caja,
                gruesa = detalle.gruesa,
                unidInd = detalle.unidInd,
                sucursal = detalle.sucursal,
                deposito = detalle.deposito
            )
        }
    }
}
