package com.gloria.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad para la tabla STKW002INV que almacena el detalle de inventarios
 * Esta tabla es idéntica a la estructura Oracle proporcionada
 */
@Entity(
    tableName = "STKW002INV",
    indices = [
        androidx.room.Index(value = ["winvd_nro_inv", "winvd_secu"], unique = true)
    ]
)
data class InventarioDetalle(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Campos principales del inventario
    val winvd_nro_inv: Int,           // Número de inventario (ID de cabecera)
    val winvd_secu: Int,              // Secuencia del artículo en el inventario
    
    // Cantidades
    val winvd_cant_act: Int,          // Cantidad actual del artículo
    val winvd_cant_inv: Int,          // Cantidad inventariada
    
    // Fechas
    val winvd_fec_vto: String,        // Fecha de vencimiento del lote
    val winve_fec: String,            // Fecha del inventario
    
    // Ubicación
    val ARDE_SUC: Int,                // Sucursal del artículo
    
    // Artículo
    val winvd_art: String,            // Código del artículo
    val art_desc: String,             // Descripción del artículo
    val winvd_lote: String,           // Lote del artículo
    
    // Área
    val winvd_area: Int,              // Código del área
    val area_desc: String,            // Descripción del área
    
    // Departamento
    val winvd_dpto: Int,              // Código del departamento
    val dpto_desc: String,            // Descripción del departamento
    
    // Sección
    val winvd_secc: Int,              // Código de la sección
    val secc_desc: String,            // Descripción de la sección
    
    // Familia
    val winvd_flia: Int,              // Código de la familia
    val flia_desc: String,            // Descripción de la familia
    
    // Grupo
    val winvd_grupo: Int,             // Código del grupo
    val grup_desc: String,            // Descripción del grupo
    
    // Subgrupo
    val winvd_subgr: Int,             // Código del subgrupo
    
    // Estado y control
    val estado: String,                // Estado del inventario
    val WINVE_LOGIN_CERRADO_WEB: String, // Usuario que cerró el inventario
    val tipo_toma: String,            // Tipo de toma (M = Manual)
    val winve_login: String,          // Usuario que creó el inventario
    val winvd_consolidado: String,    // Indicador de consolidado
    
    // Descripciones adicionales
    val desc_grupo_parcial: String,   // Descripción de grupos parciales
    val desc_familia: String,         // Descripción de familia
    
    // Ubicación adicional
    val winve_dep: String,            // Depósito del inventario
    val winve_suc: String,            // Sucursal del inventario
    
    // Información adicional
    val toma_registro: String,        // Registro de la toma
    val cod_barra: String,            // Código de barras
    val caja: Int,                    // Caja
    val GRUESA: Double,                // Gruesa
    val UNID_IND: Int,                // Unidad individual
    
    // Descripciones de ubicación
    val sucursal: String,             // Descripción de la sucursal
    val deposito: String,              // Descripción del depósito
    val stockVisible: String,         // Indicador de visibilidad del stock
    val winveTipo: String               // valor S = simultaneo  o I= individual
)
