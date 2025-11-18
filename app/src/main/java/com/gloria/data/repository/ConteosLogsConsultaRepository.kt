package com.gloria.data.repository

import com.gloria.BuildConfig
import com.gloria.data.api.ConteosLogsConsultaApiService
import com.gloria.data.model.ConteosLogPayload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ConteosLogsConsultaRepository @Inject constructor(
    private val conteosLogsConsultaApiService: ConteosLogsConsultaApiService
) {

    suspend fun obtenerConteosPorInventario(numeroInventario: Int): List<ConteosLogPayload> =
        withContext(Dispatchers.IO) {
            val token = "Bearer ${BuildConfig.TOKEN_BACKEND}"
            conteosLogsConsultaApiService.obtenerConteosPorInventario(token, numeroInventario)
        }
}

