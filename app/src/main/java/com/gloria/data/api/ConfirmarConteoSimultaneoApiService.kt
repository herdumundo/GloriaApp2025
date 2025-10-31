package com.gloria.data.api

import com.gloria.data.entity.api.ConfirmarConteoSimultaneoResponse
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ConfirmarConteoSimultaneoApiService {
    @POST("api/inventario-simultaneo/confirmacion-conteo-simultaneo/{idInventario}")
    suspend fun confirmarConteoSimultaneo(
        @Path("idInventario") idInventario: Int,
        @Query("userdb") userdb: String,
        @Query("passdb") passdb: String,
        @Query("token") token: String
    ): Response<ConfirmarConteoSimultaneoResponse>
}
