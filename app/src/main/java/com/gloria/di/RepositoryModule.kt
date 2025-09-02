package com.gloria.di

import com.gloria.data.dao.*
import com.gloria.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideCancelacionTomaDao(): CancelacionTomaDao {
        return CancelacionTomaDaoImpl()
    }

    @Provides
    @Singleton
    fun provideCancelacionTomaRepository(
        cancelacionTomaDao: CancelacionTomaDao
    ): CancelacionTomaRepository {
        return CancelacionTomaRepository(cancelacionTomaDao)
    }

    @Provides
    @Singleton
    fun provideArticuloTomaDao(): ArticuloTomaDao {
        return ArticuloTomaDaoImpl()
    }

    @Provides
    @Singleton
    fun provideArticuloTomaRepository(
        articuloTomaDao: ArticuloTomaDao
    ): ArticuloTomaRepository {
        return ArticuloTomaRepository(articuloTomaDao)
    }
}