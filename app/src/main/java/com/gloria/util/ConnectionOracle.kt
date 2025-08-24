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
        
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        
        var connection: Connection? = null

        try {
            Class.forName(driver)
            Controles.verificadorRed = 0
            DriverManager.setLoginTimeout(5)

            connection = DriverManager.getConnection(url, user, passwd)

            Controles.verificadorRed = 1
            Controles.resBD = 1
            
        } catch (se: SQLException) {
            when (se.errorCode) {
                1017 -> {
                    Controles.mensajeLogin = "USUARIO O CONTRASEÑA INCORRECTA, FAVOR VERIFIQUE."
                }
                17002, 20 -> {
                    Controles.mensajeLogin = "ERROR DE CONEXION, VERIFIQUE LA RED."
                    Controles.resBD = 2
                    Variables.userdb = user
                    Variables.passdb = passwd
                }
                17452 -> {
                    Controles.mensajeLogin = "USUARIO O CONTRASEÑA INCORRECTA, FAVOR VERIFIQUE."
                    Controles.resBD = 3
                }
                else -> {
                    Controles.mensajeLogin = se.message ?: "Error desconocido"
                    Controles.resBD = 3
                }
            }
        } catch (e: ClassNotFoundException) {
            Log.e("ConnectionOracle", "Error al cargar el driver: ${e.message}")
        }
        
        return connection
    }
}
