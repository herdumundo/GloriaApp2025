package com.gloria.util

import android.annotation.SuppressLint
import android.os.StrictMode
import android.util.Log
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

/**
 * Clase para manejar la conexión a Oracle Database
 * Convertida de Java a Kotlin
 */
object ConnectionOracle {
    
    @SuppressLint("NewApi")
    fun getConnection(): Connection? {
        val user = Variables.userdb
        //   String url = "jdbc:oracle:thin:@(DESCRIPTION= (ADDRESS=(PROTOCOL=TCP)(HOST=192.168.0.6)(PORT=1521)) (CONNECT_DATA=(SERVICE_NAME=orcl)))";
        val url =  "jdbc:oracle:thin:@(DESCRIPTION= (ADDRESS=(PROTOCOL=TCP)(HOST=192.168.0.7)(PORT=1521)) (CONNECT_DATA=(SERVICE_NAME=prueba)))"
        val passwd = Variables.passdb
        val driver = "oracle.jdbc.driver.OracleDriver"
        
        Log.d("ConnectionOracle", "=== INICIANDO getConnection ===")
        Log.d("ConnectionOracle", "Usuario: $user")
        Log.d("ConnectionOracle", "Password: ${passwd.take(3)}***")
        Log.d("ConnectionOracle", "URL: $url")
        Log.d("ConnectionOracle", "Driver: $driver")
        
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        
        var connection: Connection? = null

        try {
            Log.d("ConnectionOracle", "🔧 Cargando driver Oracle...")
            Class.forName(driver)
            Log.d("ConnectionOracle", "✅ Driver cargado correctamente")
            
            Controles.verificadorRed = 0
            DriverManager.setLoginTimeout(5)
            Log.d("ConnectionOracle", "⏱️ Timeout establecido a 5 segundos")

            Log.d("ConnectionOracle", "🔌 Intentando conectar a Oracle...")
            connection = DriverManager.getConnection(url, user, passwd)
            Log.d("ConnectionOracle", "✅ CONEXIÓN EXITOSA a Oracle!")

            Controles.verificadorRed = 1
            Controles.resBD = 1
            
        } catch (se: SQLException) {
            Log.e("ConnectionOracle", "❌ SQLException - Error Code: ${se.errorCode}")
            Log.e("ConnectionOracle", "❌ SQLException - Message: ${se.message}")
            Log.e("ConnectionOracle", "❌ SQLException - SQL State: ${se.sqlState}")
            
            when (se.errorCode) {
                1017 -> {
                    Controles.mensajeLogin = "USUARIO O CONTRASEÑA INCORRECTA, FAVOR VERIFIQUE."
                    Log.e("ConnectionOracle", "🔐 Error 1017: Usuario o contraseña incorrecta")
                }
                17002, 20 -> {
                    Controles.mensajeLogin = "ERROR DE CONEXION, VERIFIQUE LA RED."
                    Controles.resBD = 2
                    Variables.userdb = user
                    Variables.passdb = passwd
                    Log.e("ConnectionOracle", "🌐 Error 17002/20: Error de conexión de red")
                }
                17452 -> {
                    Controles.mensajeLogin = "USUARIO O CONTRASEÑA INCORRECTA, FAVOR VERIFIQUE."
                    Controles.resBD = 3
                    Log.e("ConnectionOracle", "🔐 Error 17452: Usuario o contraseña incorrecta")
                }
                else -> {
                    Controles.mensajeLogin = se.message ?: "Error desconocido"
                    Controles.resBD = 3
                    Log.e("ConnectionOracle", "❓ Error desconocido: ${se.errorCode}")
                }
            }
        } catch (e: ClassNotFoundException) {
            Log.e("ConnectionOracle", "❌ ClassNotFoundException: Error al cargar el driver: ${e.message}")
        }
        
        Log.d("ConnectionOracle", "🔚 Finalizando getConnection - connection: ${if (connection != null) "✅ OK" else "❌ NULL"}")
        return connection
    }
}
