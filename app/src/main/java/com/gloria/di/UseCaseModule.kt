package com.gloria.di

import com.gloria.data.dao.LoggedUserDao
import com.gloria.domain.usecase.GetSucursalesUseCase
import com.gloria.domain.usecase.articulo.GetArticulosTomaUseCase
import com.gloria.domain.usecase.cancelacion.CancelarTomaParcialUseCase
import com.gloria.domain.usecase.cancelacion.CancelarTomaTotalUseCase
import com.gloria.domain.usecase.cancelacion.GetCancelacionesTomaUseCase
import com.gloria.repository.ArticuloTomaRepository
import com.gloria.repository.CancelacionTomaRepository
import com.gloria.repository.SucursalRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    
    @Provides
    @Singleton
    fun provideGetSucursalesUseCase(
        loggedUserDao: LoggedUserDao,
        sucursalRepository: SucursalRepository
    ): GetSucursalesUseCase {
        return GetSucursalesUseCase(loggedUserDao, sucursalRepository)
    }
    
    @Provides
    @Singleton
    fun provideGetCancelacionesTomaUseCase(
        repository: CancelacionTomaRepository
    ): GetCancelacionesTomaUseCase {
        return GetCancelacionesTomaUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideCancelarTomaParcialUseCase(
        repository: CancelacionTomaRepository
    ): CancelarTomaParcialUseCase {
        return CancelarTomaParcialUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideCancelarTomaTotalUseCase(
        repository: CancelacionTomaRepository
    ): CancelarTomaTotalUseCase {
        return CancelarTomaTotalUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetArticulosTomaUseCase(
        repository: ArticuloTomaRepository
    ): GetArticulosTomaUseCase {
        return GetArticulosTomaUseCase(repository)
    }
}