package com.gloria.util

/**
 * Configuración de la base de datos Oracle
 */
object DatabaseConfig {
    
    // Configuración de conexión
    const val HOST = "192.168.0.7"
    const val PORT = "1521"
    const val SERVICE_NAME = "prueba"
    const val DRIVER = "oracle.jdbc.driver.OracleDriver"
    
    // Timeout de conexión en segundos
    const val LOGIN_TIMEOUT = 5
    
    // Construir la URL de conexión
    val CONNECTION_URL: String
        get() = "jdbc:oracle:thin:@(DESCRIPTION= " +
                "(ADDRESS=(PROTOCOL=TCP)(HOST=$HOST)(PORT=$PORT)) " +
                "(CONNECT_DATA=(SERVICE_NAME=$SERVICE_NAME)))"
    
    // Credenciales por defecto (puedes cambiarlas aquí)
    const val DEFAULT_USER = "admin" // Cambia por tu usuario real de Oracle
    const val DEFAULT_PASSWORD = "admin123" // Cambia por tu password real de Oracle
}
