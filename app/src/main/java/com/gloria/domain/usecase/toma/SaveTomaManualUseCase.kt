package com.gloria.domain.usecase.toma

import com.gloria.data.model.ArticuloLote
import com.gloria.data.repository.LoggedUserRepository
import com.gloria.repository.InventarioRepository
import com.gloria.repository.SincronizacionCompletaRepository
import javax.inject.Inject

/**
 * Caso de uso para guardar una toma manual
 */
class SaveTomaManualUseCase @Inject constructor(
    private val loggedUserRepository: LoggedUserRepository,
    private val inventarioRepository: InventarioRepository,
    private val sincronizacionRepository: SincronizacionCompletaRepository,
    private val insertarCabeceraYDetalleUseCase: InsertarCabeceraYDetalleInventarioUseCase
) {
    
        /**
     * Guarda una toma manual de artículos usando la nueva API
     * @param articulosSeleccionados Lista de artículos seleccionados
     * @param sucursal Sucursal seleccionada
     * @param deposito Depósito seleccionado
     * @param area Área seleccionada
     * @param departamento Departamento seleccionado
     * @param seccion Sección seleccionada
     * @param familia Familia seleccionada
     * @param subgruposSeleccionados Subgrupos seleccionados
     * @param isFamiliaTodos Si es todos los grupos de la familia
     * @param inventarioVisible Si el inventario es visible
     * @param tipoToma Tipo de toma (M = Manual, C = Criterio)
     * @param nroInventario Número del inventario (opcional, se crea uno nuevo si es null)
     */
    suspend operator fun invoke(
        articulosSeleccionados: List<ArticuloLote>,
        sucursal: Int,
        deposito: Int,
        area: Int,
        departamento: Int,
        seccion: Int,
        familia: String?,
        subgruposSeleccionados: List<Pair<Int, Int>>,
        isFamiliaTodos: Boolean,
        inventarioVisible: Boolean = true,
        tipoToma: String = "M",
        nroInventario: Int? = null
    ): Result<Int> {
        return try {
            // Obtener usuario logueado
            val loggedUser = loggedUserRepository.getLoggedUserSync()
            if (loggedUser == null) {
                return Result.failure(Exception("No hay usuario logueado"))
            }
            
            // Determinar número de inventario
            val inventarioNum = nroInventario ?: generateInventarioNumber()
            
            // Usar el nuevo UseCase para insertar cabecera y detalle via API
            val insertResult = insertarCabeceraYDetalleUseCase(
                sucursal = sucursal,
                deposito = deposito,
                area = area,
                departamento = departamento,
                seccion = seccion,
                familia = familia,
                subgruposSeleccionados = subgruposSeleccionados,
                isFamiliaTodos = isFamiliaTodos,
                userdb = loggedUser.username,
                inventarioVisible = inventarioVisible,
                articulosSeleccionados = articulosSeleccionados,
                tipoToma = tipoToma
            )
            
            insertResult.fold(
                onSuccess = { (cabeceraId, totalInsertados) ->
                    Result.success(cabeceraId)
                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Genera un número de inventario único
     */
    private suspend fun generateInventarioNumber(): Int {
        return inventarioRepository.generateInventarioNumber()
    }
}
