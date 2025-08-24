package com.gloria.util

/**
 * Clase para manejar controles de estado de la aplicación
 */
object Controles {
    var verificadorRed: Int = 0
    var resBD: Int = 0
    var mensajeLogin: String = ""
    
    // Constantes para los códigos de estado
    const val CONEXION_EXITOSA = 1
    const val ERROR_RED = 2
    const val ERROR_CREDENCIALES = 3
    const val RED_OK = 1
    const val RED_ERROR = 0
}
