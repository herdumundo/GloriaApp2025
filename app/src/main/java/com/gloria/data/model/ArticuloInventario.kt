package com.gloria.data.model

/**
 * Modelo de datos para los artículos del inventario
 * Mapea los campos del query para el conteo físico
 */
data class ArticuloInventario(
    val winvd_nro_inv: Int,                    // Número de inventario
    val artDesc: String,                        // Descripción del artículo
    val winvdLote: String,                      // Lote del artículo
    val winvdArt: String,                       // Código del artículo
    val winvdFecVto: String,                    // Fecha de vencimiento (formateada)
    val winvdArea: Int,                         // Código del área
    val winvdDpto: Int,                         // Código del departamento
    val winvdSecc: Int,                         // Código de la sección
    val winvdFlia: Int,                         // Código de la familia
    val winvdGrupo: Int,                        // Código del grupo
    val winvdCantAct: Int,                      // Cantidad actual
    val winvdCantInv: Int,                      // Cantidad inventariada
    val winvdSecu: Int,                         // Secuencial
    val grupDesc: String,                       // Descripción del grupo
    val fliaDesc: String,                       // Descripción de la familia
    val tomaRegistro: String,                   // Registro de la toma
    val codBarra: String,                       // Código de barras
    val caja: Int,                              // Cantidad por caja
    val gruesa: Int                             // Cantidad por gruesa
)
