package com.gloria.data.repository

import android.util.Log
import com.gloria.BuildConfig
import com.gloria.data.api.ConteosLogsApiService
import com.gloria.data.model.ConteosLogPayload
import com.gloria.data.model.ConteosLogsBulkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ConteosLogsApiRepository @Inject constructor(
    private val conteosLogsApiService: ConteosLogsApiService
) {

    suspend fun enviarConteosLogs(logs: List<ConteosLogPayload>): ConteosLogsBulkResponse =
        withContext(Dispatchers.IO) {
            val token = "Bearer ${BuildConfig.TOKEN_BACKEND}"
             conteosLogsApiService.enviarConteosLogs(token, logs)
        }
}

