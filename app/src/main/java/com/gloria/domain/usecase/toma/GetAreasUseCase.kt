package com.gloria.domain.usecase.toma

import com.gloria.data.entity.Area
import com.gloria.data.repository.AreaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAreasUseCase @Inject constructor(
    private val areaRepository: AreaRepository
) {
    suspend operator fun invoke(): Flow<List<Area>> {
        return areaRepository.getAllAreas()
    }
}
