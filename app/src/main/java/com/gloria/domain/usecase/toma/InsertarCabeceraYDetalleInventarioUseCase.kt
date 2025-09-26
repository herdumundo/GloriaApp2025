package com.gloria.domain.usecase.toma

import com.gloria.data.repository.InsertarCabeceraYDetalleApiRepository
import com.gloria.data.repository.LoggedUserRepository
import com.gloria.data.entity.api.InsertarCabeceraYDetalleRequest
import com.gloria.data.entity.api.CabeceraInventario
import com.gloria.data.entity.api.DetalleInventario
import com.gloria.data.model.ArticuloLote
import android.util.Log
import javax.inject.Inject

class InsertarCabeceraYDetalleInventarioUseCase @Inject constructor(
    private val insertarCabeceraYDetalleApiRepository: InsertarCabeceraYDetalleApiRepository,
    private val loggedUserRepository: LoggedUserRepository
) {
    
    suspend operator fun invoke(
        sucursal: Int,
        deposito: Int,
        area: Int,
        departamento: Int,
        seccion: Int,
        familia: String?,
        subgruposSeleccionados: List<Pair<Int, Int>>,
        isFamiliaTodos: Boolean,
        userdb: String,
        inventarioVisible: Boolean,
        articulosSeleccionados: List<ArticuloLote>,
        tipoToma: String = "M", // "M" = Manual, "C" = Criterio
        onProgressUpdate: ((current: Int, total: Int) -> Unit)? = null
    ): Result<Pair<Int, Int>> {
        return try {
            Log.d("InsertarCabeceraYDetalleUseCase", "🚀 INICIANDO inserción de cabecera y detalle")
            Log.d("InsertarCabeceraYDetalleUseCase", "📊 Parámetros: suc=$sucursal, dep=$deposito, area=$area, dpto=$departamento, secc=$seccion, flia=$familia")
            Log.d("InsertarCabeceraYDetalleUseCase", "📋 Artículos seleccionados: ${articulosSeleccionados.size}")
            
            // Obtener usuario logueado para las credenciales
            val loggedUser = loggedUserRepository.getLoggedUserSync()
            if (loggedUser == null) {
                Log.e("InsertarCabeceraYDetalleUseCase", "❌ No hay usuario logueado")
                return Result.failure(Exception("No hay usuario logueado"))
            }
            
            // Crear cabecera
            val cabecera = CabeceraInventario(
                sucursal = sucursal,
                deposito = deposito,
                area = area,
                departamento = departamento,
                seccion = seccion,
                idFamilia = familia ?: "0",
                idGrupo = subgruposSeleccionados.firstOrNull()?.first?.toString() ?: "0",
                gruposParcial = if (isFamiliaTodos) "S" else "N",
                inventarioVisible = inventarioVisible,
                tipoToma = tipoToma
            )
            
            // Crear detalles
            val detalles = articulosSeleccionados.map { articulo ->
                DetalleInventario(
                    artCodigo = articulo.artCodigo,
                    cantidadActual = articulo.cantidad,
                    lote = articulo.ardeLote,
                    fechaVencimiento = articulo.ardeFecVtoLote,
                    area = area,
                    departamento = departamento,
                    seccion = seccion,
                    familia = articulo.fliaCodigo.toIntOrNull() ?: 0,
                    grupo = articulo.grupCodigo,
                    subgrupo = articulo.sugrCodigo,
                    consolidado = "N"
                )
            }
            
            // Crear request
            val request = InsertarCabeceraYDetalleRequest(
                userdb = loggedUser.username,
                passdb = loggedUser.password,
                cabecera = cabecera,
                detalle = detalles
            )
            
            Log.d("InsertarCabeceraYDetalleUseCase", "🌐 Enviando request al backend...")
            
            // Llamar a la API
            val result = insertarCabeceraYDetalleApiRepository.insertarCabeceraYDetalle(request)
            
            result.fold(
                onSuccess = { response ->
                    Log.d("InsertarCabeceraYDetalleUseCase", "✅ Inserción exitosa: ${response.message}")
                    Log.d("InsertarCabeceraYDetalleUseCase", "🎯 ID Cabecera: ${response.idCabecera}")
                    Log.d("InsertarCabeceraYDetalleUseCase", "📊 Total insertados: ${response.totalArticulosInsertados}")
                    Log.d("InsertarCabeceraYDetalleUseCase", "📋 Total procesados: ${response.totalArticulosProcesados}")
                    Result.success(Pair(response.idCabecera, response.totalArticulosInsertados))
                },
                onFailure = { error ->
                    Log.e("InsertarCabeceraYDetalleUseCase", "❌ Error en inserción: ${error.message}")
                    Result.failure(error)
                }
            )
            
        } catch (e: Exception) {
            Log.e("InsertarCabeceraYDetalleUseCase", "❌ ERROR GENERAL: ${e.message}")
            Result.failure(e)
        }
    }
}
