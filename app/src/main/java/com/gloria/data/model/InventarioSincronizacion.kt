package com.gloria.data.model

/**
 * Modelo de datos para la sincronización de inventarios desde Oracle
 * Mapea exactamente los campos del query de sincronización
 */
data class InventarioSincronizacion(
    val toma: String,                    // 'A' AS toma
    val invd_cant_inv: Int,             // 0 AS invd_cant_inv
    val ART_DESC: String,               // ART_DESC
    val ARDE_SUC: Int,                  // ARDE_SUC
    val winvd_nro_inv: Int,             // winvd_nro_inv
    val winvd_art: String,              // winvd_art
    val winvd_lote: String,             // b.WINVD_LOTE AS winvd_lote
    val winvd_fec_vto: String,          // b.winvd_fec_vto AS winvd_fec_vto
    val winvd_area: Int,                // winvd_area
    val winvd_dpto: Int,                // winvd_dpto
    val winvd_secc: Int,                // winvd_secc
    val winvd_flia: Int,                // winvd_flia
    val winvd_grupo: Int,               // winvd_grupo
    val winvd_cant_act: Int,            // 0 AS winvd_cant_act
    val winve_fec: String,              // winve_fec
    val dpto_desc: String,              // dpto_desc
    val secc_desc: String,              // secc_desc
    val flia_desc: String,              // flia_desc
    val grup_desc: String,              // grup_desc
    val area_desc: String,              // area_desc
    val sugr_codigo: Int,               // sugr_codigo
    val winvd_secu: Int,                // winvd_secu AS winvd_secu
    val tipo_toma: String,              // CASE WHEN c.winve_tipo_toma = 'C' THEN 'CRITERIO' ELSE 'MANUAL' END AS tipo_toma
    val winve_login: String,            // winve_login
    val winvd_consolidado: String,      // '' AS winvd_consolidado
    val desc_grupo_parcial: String,     // CASE WHEN c.winve_grupo IS NULL AND c.winve_grupo_parcial IS NULL THEN 'TODOS' WHEN c.winve_grupo_parcial IS NOT NULL THEN 'PARCIALES' ELSE grup_desc END AS desc_grupo_parcial
    val desc_familia: String,           // CASE WHEN c.winve_flia IS NULL THEN 'TODAS' ELSE a.flia_desc END AS desc_familia
    val winve_dep: String,              // winve_dep
    val winve_suc: String,              // winve_suc
    val coba_codigo_barra: String,      // a.coba_codigo_barra
    val caja: Int,                      // a.caja
    val GRUESA: Int,                    // a.GRUESA
    val UNID_IND: Int,                  // a.UNID_IND
    val SUC_DESC: String,               // suc.SUC_DESC
    val DEP_DESC: String                // suc.DEP_DESC
)
