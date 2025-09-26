package com.gloria.data.repository

import com.gloria.data.model.ArticuloLote
import com.gloria.data.entity.api.ArticuloClasificacionApi
import com.gloria.domain.usecase.AuthSessionUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArticuloLoteRepository @Inject constructor(
    private val authSessionUseCase: AuthSessionUseCase,
    private val articulosClasificacionApiRepository: ArticulosClasificacionApiRepository
) {
    
    suspend fun getArticulosLotes(
        subgruposSeleccionados: List<Pair<Int, Int>>, // (grupCodigo, sugrCodigo)
        sucursal: Int,
        deposito: Int,
        area: Int,
        departamento: Int,
        seccion: Int,
        familia: String,
        isFamiliaTodos: Boolean = false,
        isGruposTodos: Boolean = false,
        onProgressUpdate: ((current: Int, total: Int) -> Unit)? = null
    ): Flow<List<ArticuloLote>> = flow {
        Log.d("ArticuloLoteRepository", "🎯 MÉTODO getArticulosLotes() LLAMADO CON API")
        Log.d("ArticuloLoteRepository", "📞 Llamada desde ViewModel recibida")
        
        try {
            Log.d("ArticuloLoteRepository", "🔄 INICIANDO consulta API...")
            Log.d("ArticuloLoteRepository", "📊 Parámetros: suc=$sucursal, dep=$deposito, area=$area, dpto=$departamento, secc=$seccion, flia=$familia, isFamiliaTodos=$isFamiliaTodos, isGruposTodos=$isGruposTodos")
            
            // Obtener credenciales del usuario logueado
            val loggedUser = authSessionUseCase.getCurrentUser()
            if (loggedUser == null) {
                Log.e("ArticuloLoteRepository", "❌ No hay usuario logueado")
                emit(emptyList())
                return@flow
            }
            
            Log.d("ArticuloLoteRepository", "👤 Usuario logueado: ${loggedUser.username}")
            
            // Actualizar progreso inicial
            onProgressUpdate?.invoke(0, 0)
            
            // Llamar a la API según el tipo de consulta
            val apiResult = when {
                isFamiliaTodos -> {
                    Log.d("ArticuloLoteRepository", "🌐 Consultando: Todas las familias")
                    articulosClasificacionApiRepository.getArticulosTodasFamilias(
                        userdb = loggedUser.username,
                        passdb = loggedUser.password,
                        ardeSuc = sucursal,
                        ardeDep = deposito,
                        areaCodigo = area,
                        dptoCodigo = departamento,
                        seccCodigo = seccion
                    )
                }
                isGruposTodos -> {
                    Log.d("ArticuloLoteRepository", "🌐 Consultando: Todos los grupos de familia $familia")
                    articulosClasificacionApiRepository.getArticulosTodosGruposFamilia(
                        userdb = loggedUser.username,
                        passdb = loggedUser.password,
                        ardeSuc = sucursal,
                        ardeDep = deposito,
                        areaCodigo = area,
                        dptoCodigo = departamento,
                        seccCodigo = seccion,
                        fliaCodigo = familia.toIntOrNull() ?: 0
                    )
                }
                else -> {
                    Log.d("ArticuloLoteRepository", "🌐 Consultando: Grupos específicos")
                    val subgruposCodigos = subgruposSeleccionados.map { it.second }
                    val gruposCodigos = subgruposSeleccionados.map { it.first }.distinct()
                    
                    articulosClasificacionApiRepository.getArticulosGruposEspecificos(
                        userdb = loggedUser.username,
                        passdb = loggedUser.password,
                        ardeSuc = sucursal,
                        ardeDep = deposito,
                        areaCodigo = area,
                        dptoCodigo = departamento,
                        seccCodigo = seccion,
                        fliaCodigo = familia.toIntOrNull() ?: 0,
                        subgruposSeleccionados = subgruposCodigos,
                        gruposCodigos = gruposCodigos
                    )
                }
            }
            
            if (apiResult.isFailure) {
                Log.e("ArticuloLoteRepository", "❌ Error en API: ${apiResult.exceptionOrNull()?.message}")
                emit(emptyList())
                return@flow
            }
            
            val response = apiResult.getOrNull()!!
            Log.d("ArticuloLoteRepository", "✅ Respuesta API exitosa: ${response.length} artículos")
            
            // Actualizar progreso
            onProgressUpdate?.invoke(0, response.length)
            
            // Convertir respuesta API a ArticuloLote
            val articulosLotes = response.data.mapIndexed { index, articuloApi ->
                // Actualizar progreso durante la conversión
                onProgressUpdate?.invoke(index + 1, response.length)
                
                convertirArticuloApiToArticuloLote(articuloApi)
            }
            
            Log.d("ArticuloLoteRepository", "✅ Conversión completada: ${articulosLotes.size} artículos convertidos")
            emit(articulosLotes)
            
        } catch (e: Exception) {
            // Verificar si el Flow fue cancelado
            if (e is kotlinx.coroutines.CancellationException) {
                Log.d("ArticuloLoteRepository", "🔄 Flow cancelado - operación interrumpida")
                return@flow
            }
            Log.e("ArticuloLoteRepository", "❌ ERROR GENERAL en getArticulosLotes API: ${e.message}")
            Log.e("ArticuloLoteRepository", "Stack trace: ${e.stackTraceToString()}")
            emit(emptyList())
        }
    }
    
    /**
     * Convierte un ArticuloClasificacionApi a ArticuloLote
     */
    private fun convertirArticuloApiToArticuloLote(articuloApi: ArticuloClasificacionApi): ArticuloLote {
        // Generar concatID combinando códigos
        val concatID = "${articuloApi.artCodigo}_${articuloApi.ardeLote}_${articuloApi.ardeSuc}_${articuloApi.ardeDep}"
        
        // Formatear fecha de vencimiento (ISO a formato DD-MM-YYYY)
        val fechaVencimiento = try {
            val isoDate = articuloApi.ardeFecVtoLote
            if (isoDate.contains("T")) {
                // Formato ISO: 2026-03-28T04:00:00.000+00:00
                val datePart = isoDate.split("T")[0]
                val parts = datePart.split("-")
                if (parts.size == 3) {
                    "${parts[2]}-${parts[1]}-${parts[0]}"
                } else {
                    isoDate
                }
            } else {
                isoDate
            }
        } catch (e: Exception) {
            articuloApi.ardeFecVtoLote
        }
        
        return ArticuloLote(
            concatID = concatID,
            cantidad = articuloApi.ardeCantAct,
            vencimiento = fechaVencimiento,
            fliaCodigo = articuloApi.fliaCodigo.toString(),
            grupCodigo = articuloApi.grupCodigo,
            grupDesc = articuloApi.grupDesc,
            fliaDesc = articuloApi.fliaDesc,
            artDesc = articuloApi.artDesc,
            ardeLote = articuloApi.ardeLote,
            artCodigo = articuloApi.artCodigo.toString(),
            ardeFecVtoLote = fechaVencimiento,
            sugrCodigo = articuloApi.sugrCodigo,
            sugrDesc = articuloApi.sugrDesc
        )
    }
}
