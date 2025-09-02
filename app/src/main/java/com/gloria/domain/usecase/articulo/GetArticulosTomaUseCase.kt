package com.gloria.domain.usecase.articulo

import com.gloria.data.model.ArticuloToma
import com.gloria.repository.ArticuloTomaRepository
import javax.inject.Inject

class GetArticulosTomaUseCase @Inject constructor(
    private val articuloTomaRepository: ArticuloTomaRepository
) {
    suspend operator fun invoke(nroToma: Int): Result<List<ArticuloToma>> {
        return try {
            val articulos = articuloTomaRepository.getArticulosToma(nroToma)
            Result.success(articulos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}