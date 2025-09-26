package com.gloria.di

import android.content.Context
import com.gloria.data.AppDatabase
import com.gloria.data.dao.*
import com.gloria.domain.usecase.AuthSessionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Dagger Hilt para la base de datos
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }
    
    @Provides
    fun provideLoggedUserDao(database: AppDatabase): LoggedUserDao {
        return database.loggedUserDao()
    }
    
    @Provides
    fun provideInventarioDetalleDao(database: AppDatabase): InventarioDetalleDao {
        return database.inventarioDetalleDao()
    }
    
    @Provides
    fun provideAreaDao(database: AppDatabase): AreaDao {
        return database.areaDao()
    }
    
    @Provides
    fun provideDepartamentoDao(database: AppDatabase): DepartamentoDao {
        return database.departamentoDao()
    }
    
    @Provides
    fun provideSeccionDao(database: AppDatabase): SeccionDao {
        return database.seccionDao()
    }
    
    @Provides
    fun provideFamiliaDao(database: AppDatabase): FamiliaDao {
        return database.familiaDao()
    }
    
    @Provides
    fun provideGrupoDao(database: AppDatabase): GrupoDao {
        return database.grupoDao()
    }
    
    @Provides
    fun provideSubgrupoDao(database: AppDatabase): SubgrupoDao {
        return database.subgrupoDao()
    }
    
    @Provides
    fun provideSucursalDepartamentoDao(database: AppDatabase): SucursalDepartamentoDao {
        return database.sucursalDepartamentoDao()
    }

    @Provides
    fun provideArticuloTomaDao(
        authSessionUseCase: AuthSessionUseCase,
        productosApiRepository: com.gloria.data.repository.ProductosInventarioPorNumeroApiRepository
    ): ArticuloTomaDao {
        return ArticuloTomaDaoImpl(authSessionUseCase, productosApiRepository)
    }

    @Provides
    fun provideCancelacionTomaDao(
        authSessionUseCase: AuthSessionUseCase,
        inventariosPendientesRepo: com.gloria.data.repository.InventariosPendientesPorUsuarioApiRepository,
        cancelarTomaApiRepository: com.gloria.data.repository.CancelarTomaApiRepository
    ): CancelacionTomaDao {
        return CancelacionTomaDaoImpl(authSessionUseCase, inventariosPendientesRepo, cancelarTomaApiRepository)
    }

    @Provides
    fun provideUserPermissionDao(database: AppDatabase): UserPermissionDao {
        return database.userPermissionDao()
    }

    @Provides
    fun provideUserPermissionOracleDao(authSessionUseCase: AuthSessionUseCase): UserPermissionOracleDao {
        return UserPermissionOracleDao(authSessionUseCase)
    }
}

