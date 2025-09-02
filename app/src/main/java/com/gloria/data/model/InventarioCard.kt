package com.gloria.data.model

/**
 * Modelo de datos para los cards de inventario en la pantalla de registro
 * Mapea los campos del query DISTINCT para mostrar en los cards
 */
data class InventarioCard(
    val winvd_nro_inv: Int,                    // Número de inventario
    val fecha_toma: String,                    // Fecha de la toma (formateada como dd/mm/yyyy HH:mm)
    val area_desc: String,                     // Área del inventario
    val dpto_desc: String,                     // Departamento del inventario
    val tipo_toma: String,                     // Tipo de toma (MANUAL, CRITERIO)
    val secc_desc: String,                     // Sección del inventario
    val winvd_consolidado: String,             // Indicador de consolidado
    val desc_grupo_parcial: String,            // Descripción de grupo parcial
    val desc_familia: String,                  // Descripción de familia
    val sucursal: String,                      // Descripción de la sucursal
    val deposito: String,                      // Descripción del depósito
    val estado: String                         // Estado del inventario (A=Activo, P=Procesado)
)
