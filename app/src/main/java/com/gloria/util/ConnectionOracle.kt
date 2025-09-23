package com.gloria.util

import android.annotation.SuppressLint
import android.os.StrictMode
import android.util.Log
import android.content.Context
import com.gloria.domain.usecase.AuthSessionUseCase
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

/**
 * Clase para manejar la conexión a Oracle Database
 * Convertida de Java a Kotlin
 */
object ConnectionOracle {
    
    /**
     * Obtiene las credenciales del usuario logueado desde la base de datos local
     */
    private suspend fun obtenerCredencialesUsuario(authSessionUseCase: AuthSessionUseCase): Pair<String, String>? {
        return try {
            val loggedUser = authSessionUseCase.getCurrentUser()
            
            if (loggedUser != null && !loggedUser.username.isNullOrBlank() && !loggedUser.password.isNullOrBlank()) {
                Log.d("PROCESO_LOGIN", "🔍 [CREDENCIALES] Usuario obtenido: ${loggedUser.username}")
                Log.d("PROCESO_LOGIN", "🔍 [CREDENCIALES] Password: ${loggedUser.password.take(3)}***")
                Pair(loggedUser.username, loggedUser.password)
            } else {
                Log.e("PROCESO_LOGIN", "❌ [CREDENCIALES] No hay usuario logueado o credenciales vacías")
                null
            }
        } catch (e: Exception) {
            Log.e("PROCESO_LOGIN", "❌ [CREDENCIALES] Error al obtener credenciales: ${e.message}")
            null
        }
    }
    
    @SuppressLint("NewApi")
    suspend fun getConnection(authSessionUseCase: AuthSessionUseCase): Connection? {
        Log.d("PROCESO_LOGIN", "🔍 [CON_AUTH] Variables.userdb: '${Variables.userdb}'")
        Log.d("PROCESO_LOGIN", "🔍 [CON_AUTH] Variables.passdb: '${Variables.passdb.take(3)}***'")
        
        // PRIORIDAD 1: Si hay variables globales configuradas (login reciente), usarlas primero
        if (!Variables.userdb.isBlank() && !Variables.passdb.isBlank()) {
            Log.d("PROCESO_LOGIN", "🔍 [CON_AUTH] Usando variables globales (login reciente)")
            return getConnection(Variables.userdb, Variables.passdb)
        }
        
        // PRIORIDAD 2: Si no hay variables globales, intentar obtener credenciales desde la base de datos local
        val credenciales = obtenerCredencialesUsuario(authSessionUseCase)
        
        if (credenciales != null) {
            Log.d("PROCESO_LOGIN", "🔍 [CON_AUTH] Usando credenciales de usuario logueado (sesión persistente)")
            return getConnection(credenciales.first, credenciales.second)
        }
        
        Log.e("PROCESO_LOGIN", "❌ [CON_AUTH] No hay credenciales disponibles")
        throw Exception("No hay credenciales disponibles. Usuario no logueado y variables globales vacías.")
    }
    
    @SuppressLint("NewApi")
    fun getConnection(): Connection? {
        Log.d("PROCESO_LOGIN", "🔍 [SIN_PARAMETROS] Variables.userdb: '${Variables.userdb}'")
        Log.d("PROCESO_LOGIN", "🔍 [SIN_PARAMETROS] Variables.passdb: '${Variables.passdb.take(3)}***'")
        
        // Si las variables globales están vacías, lanzar error
        if (Variables.userdb.isBlank() || Variables.passdb.isBlank()) {
            Log.e("PROCESO_LOGIN", "❌ [SIN_PARAMETROS] Variables globales vacías")
            throw Exception("Variables de conexión no configuradas. Use getConnection(context) o getConnection(user, passwd)")
        }
        
        return getConnection(Variables.userdb, Variables.passdb)
    }
    
    @SuppressLint("NewApi")
    fun getConnection(user: String, passwd: String): Connection? {
        Log.d("PROCESO_LOGIN", "=== INICIANDO getConnection CON PARÁMETROS ===")
        Log.d("PROCESO_LOGIN", "🔍 Parámetro user recibido: '$user'")
        Log.d("PROCESO_LOGIN", "🔍 Parámetro passwd recibido: '${passwd.take(3)}***'")
        Log.d("PROCESO_LOGIN", "🔍 user es null: ${user == null}")
        Log.d("PROCESO_LOGIN", "🔍 passwd es null: ${passwd == null}")
        
        // Validar parámetros
        if (user.isNullOrBlank()) {
            Log.e("PROCESO_LOGIN", "❌ ERROR: Usuario es null o vacío")
            return null
        }
        
        if (passwd.isNullOrBlank()) {
            Log.e("PROCESO_LOGIN", "❌ ERROR: Password es null o vacío")
            return null
        }

        //   String url = "jdbc:oracle:thin:@(DESCRIPTION= (ADDRESS=(PROTOCOL=TCP)(HOST=192.168.0.6)(PORT=1521)) (CONNECT_DATA=(SERVICE_NAME=orcl)))";
        val url =  "jdbc:oracle:thin:@(DESCRIPTION= (ADDRESS=(PROTOCOL=TCP)(HOST=192.168.0.7)(PORT=1521)) (CONNECT_DATA=(SERVICE_NAME=prueba)))"
        val driver = "oracle.jdbc.driver.OracleDriver"
        
        Log.d("PROCESO_LOGIN", "=== INICIANDO getConnection ===")
        Log.d("PROCESO_LOGIN", "🔄 Hilo actual: ${Thread.currentThread().name}")
        Log.d("PROCESO_LOGIN", "Usuario: $user")
        Log.d("PROCESO_LOGIN", "Password: ${passwd.take(3)}***")
        Log.d("PROCESO_LOGIN", "URL: $url")
        Log.d("PROCESO_LOGIN", "Driver: $driver")
        
        // ❌ REMOVIDO: StrictMode permitía operaciones de red en hilo principal
        // val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        // StrictMode.setThreadPolicy(policy)
        // ✅ AHORA: Las operaciones de red se ejecutan en Dispatchers.IO
        
        var connection: Connection? = null

        try {
            Log.d("PROCESO_LOGIN", "🔧 Cargando driver Oracle...")
            Class.forName(driver)
            Log.d("PROCESO_LOGIN", "✅ Driver cargado correctamente")
            
            Controles.verificadorRed = 0
            DriverManager.setLoginTimeout(5)
            Log.d("PROCESO_LOGIN", "⏱️ Timeout establecido a 5 segundos")

            Log.d("PROCESO_LOGIN", "🔌 Intentando conectar a Oracle...")
            connection = DriverManager.getConnection(url, user, passwd)
            Log.d("PROCESO_LOGIN", "✅ CONEXIÓN EXITOSA a Oracle!")

            Controles.verificadorRed = 1
            Controles.resBD = 1
            
        } catch (se: SQLException) {
            Log.e("PROCESO_LOGIN", "❌ SQLException - Error Code: ${se.errorCode}")
            Log.e("PROCESO_LOGIN", "❌ SQLException - Message: ${se.message}")
            Log.e("PROCESO_LOGIN", "❌ SQLException - SQL State: ${se.sqlState}")
            
            when (se.errorCode) {
                1017 -> {
                    Controles.mensajeLogin = "USUARIO O CONTRASEÑA INCORRECTA, FAVOR VERIFIQUE."
                    Log.e("PROCESO_LOGIN", "🔐 Error 1017: Usuario o contraseña incorrecta")
                }
                17002, 20 -> {
                    Controles.mensajeLogin = "ERROR DE CONEXION, VERIFIQUE LA RED."
                    Controles.resBD = 2
                    Variables.userdb = user
                    Variables.passdb = passwd
                    Log.e("PROCESO_LOGIN", "🌐 Error 17002/20: Error de conexión de red")
                }
                17452 -> {
                    Controles.mensajeLogin = "USUARIO O CONTRASEÑA INCORRECTA, FAVOR VERIFIQUE."
                    Controles.resBD = 3
                    Log.e("PROCESO_LOGIN", "🔐 Error 17452: Usuario o contraseña incorrecta")
                }
                else -> {
                    Controles.mensajeLogin = se.message ?: "Error desconocido"
                    Controles.resBD = 3
                    Log.e("PROCESO_LOGIN", "❓ Error desconocido: ${se.errorCode}")
                }
            }
        } catch (e: ClassNotFoundException) {
            Log.e("PROCESO_LOGIN", "❌ ClassNotFoundException: Error al cargar el driver: ${e.message}")
        } catch (e: Exception) {
            Log.e("PROCESO_LOGIN", "❌ EXCEPCIÓN GENERAL en getConnection: ${e.message}")
            Log.e("PROCESO_LOGIN", "Stack trace: ${e.stackTraceToString()}")
        }
        
        Log.d("PROCESO_LOGIN", "🔚 Finalizando getConnection - connection: ${if (connection != null) "✅ OK" else "❌ NULL"}")
        return connection
    }
}
