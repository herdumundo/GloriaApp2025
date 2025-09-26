package com.gloria.data.dao

import android.util.Log
import com.gloria.data.model.CancelacionToma
import com.gloria.data.repository.InventariosPendientesPorUsuarioApiRepository
import com.gloria.domain.usecase.AuthSessionUseCase
import com.gloria.util.ConnectionOracle
import com.gloria.data.repository.CancelarTomaApiRepository
import com.gloria.data.entity.api.CancelarTomaRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.gloria.data.entity.api.toCancelacionToma

class CancelacionTomaDaoImpl @Inject constructor(
    private val authSessionUseCase: AuthSessionUseCase,
    private val inventariosPendientesRepo: InventariosPendientesPorUsuarioApiRepository,
    private val cancelarTomaApiRepository: CancelarTomaApiRepository
) : CancelacionTomaDao {
    override suspend fun getCancelacionesToma(userLogin: String): List<CancelacionToma> {
        return withContext<List<CancelacionToma>>(Dispatchers.IO) {
            Log.d("PROCESO_LOGIN", "=== INICIANDO getCancelacionesToma (API) ===")
            val user = authSessionUseCase.getCurrentUser() ?: throw Exception("No hay usuario logueado")
            val apiRes = inventariosPendientesRepo.getInventariosPendientes(
                userdb = user.username,
                passdb = user.password,
                usuarioCreador = userLogin.uppercase()
            )
            if (apiRes.isFailure) throw apiRes.exceptionOrNull() ?: Exception("Error desconocido API")

            val data = apiRes.getOrThrow().data
            val result = data.map { it.toCancelacionToma() }
            Log.d("PROCESO_LOGIN", "📊 Resultados obtenidos: ${result.size} cancelaciones")
            result
        }
    }

    override suspend fun cancelarTomaParcial(nroToma: Int, secuencias: List<String>): Int {
        return withContext(Dispatchers.IO) {
            val user = authSessionUseCase.getCurrentUser() ?: throw Exception("No hay usuario logueado")
            val secuenciasInt = secuencias.mapNotNull { it.toIntOrNull() }
            val request = CancelarTomaRequest(
                userdb = user.username,
                passdb = user.password,
                winveNumero = nroToma,
                parcial = true,
                usuarioQueCancela = user.username,
                secuencias = if (secuenciasInt.isNotEmpty()) secuenciasInt else listOf(0)
            )
            val res = cancelarTomaApiRepository.cancelarToma(request)
            if (res.isFailure) throw res.exceptionOrNull() ?: Exception("Error API")
            res.getOrThrow().filasAfectadas
        }
    }

    override suspend fun cancelarTomaTotal(nroToma: Int, userLogin: String): Int {
        return withContext(Dispatchers.IO) {
            val user = authSessionUseCase.getCurrentUser() ?: throw Exception("No hay usuario logueado")
            val request = CancelarTomaRequest(
                userdb = user.username,
                passdb = user.password,
                winveNumero = nroToma,
                parcial = false,
                usuarioQueCancela = userLogin,
                secuencias = listOf(0)
            )
            val res = cancelarTomaApiRepository.cancelarToma(request)
            if (res.isFailure) throw res.exceptionOrNull() ?: Exception("Error API")
            res.getOrThrow().filasAfectadas
        }
    }
}