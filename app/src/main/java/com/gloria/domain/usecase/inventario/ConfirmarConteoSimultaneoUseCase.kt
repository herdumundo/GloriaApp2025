package com.gloria.domain.usecase.inventario

import com.gloria.data.entity.api.ConfirmarConteoSimultaneoResponse
import com.gloria.data.repository.ConfirmarConteoSimultaneoRepository
import javax.inject.Inject

class ConfirmarConteoSimultaneoUseCase @Inject constructor(
    private val repository: ConfirmarConteoSimultaneoRepository
) {
    suspend operator fun invoke(idInventario: Int): Result<ConfirmarConteoSimultaneoResponse> {
        return repository.confirmarConteoSimultaneo(idInventario)
    }
}
