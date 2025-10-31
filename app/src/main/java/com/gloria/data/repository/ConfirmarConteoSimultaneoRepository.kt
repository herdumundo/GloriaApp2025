package com.gloria.data.repository

import com.gloria.BuildConfig
import com.gloria.data.api.ConfirmarConteoSimultaneoApiService
import com.gloria.data.entity.api.ConfirmarConteoSimultaneoResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfirmarConteoSimultaneoRepository @Inject constructor(
    private val apiService: ConfirmarConteoSimultaneoApiService,
    private val loggedUserRepository: LoggedUserRepository
) {
    suspend fun confirmarConteoSimultaneo(idInventario: Int): Result<ConfirmarConteoSimultaneoResponse> {
        return try {
            val loggedUser = loggedUserRepository.getLoggedUserSync()

            val response = apiService.confirmarConteoSimultaneo(
                idInventario = idInventario,
                userdb = loggedUser?.username ?: "",
                passdb = loggedUser?.password ?: "",
                token = BuildConfig.TOKEN_BACKEND
            )

            if (response.isSuccessful && response.body() != null) {
                val responseBody = response.body()!!
                if (responseBody.success) {
                    Result.success(responseBody)
                } else {
                    Result.failure(Exception(responseBody.message))
                }
            } else {
                Result.failure(Exception("Error en la respuesta del servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
