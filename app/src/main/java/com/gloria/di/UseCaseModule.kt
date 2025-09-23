package com.gloria.di

import com.gloria.domain.usecase.GetSucursalesUseCase
import com.gloria.domain.usecase.auth.LoginUseCase
import com.gloria.domain.usecase.auth.LogoutUseCase
import com.gloria.domain.usecase.auth.RegisterUseCase
import com.gloria.domain.usecase.AuthSessionUseCase
import com.gloria.domain.usecase.cancelacion.CancelarTomaParcialUseCase
import com.gloria.domain.usecase.cancelacion.CancelarTomaTotalUseCase
import com.gloria.domain.usecase.cancelacion.GetCancelacionesTomaUseCase
import com.gloria.domain.usecase.articulo.GetArticulosTomaUseCase
import com.gloria.domain.usecase.toma.SaveTomaManualUseCase
import com.gloria.domain.usecase.toma.InsertarCabeceraInventarioUseCase
import com.gloria.domain.usecase.toma.InsertarDetalleInventarioUseCase
import com.gloria.domain.usecase.toma.InsertarCabeceraYDetalleInventarioUseCase
import com.gloria.domain.usecase.toma.GetArticulosLotesUseCase
 import com.gloria.domain.usecase.toma.GetAreasUseCase
import com.gloria.domain.usecase.toma.GetDepartamentosUseCase
import com.gloria.domain.usecase.toma.GetSeccionesUseCase
import com.gloria.domain.usecase.toma.GetFamiliasUseCase
import com.gloria.domain.usecase.toma.GetGruposUseCase
import com.gloria.domain.usecase.toma.GetSucursalesUseCase as GetSucursalesTomaUseCase
 import com.gloria.repository.AuthRepository
import com.gloria.repository.InventarioRepository
import com.gloria.repository.SincronizacionCompletaRepository
import com.gloria.data.repository.AreaRepository
import com.gloria.data.repository.DepartamentoRepository
import com.gloria.data.repository.SeccionRepository
import com.gloria.data.repository.FamiliaRepository
import com.gloria.data.repository.GrupoRepository
import com.gloria.data.repository.SubgrupoRepository
import com.gloria.data.repository.SucursalDepartamentoRepository
import com.gloria.data.repository.LoggedUserRepository
import com.gloria.data.repository.AuthSessionRepository
import com.gloria.data.repository.CancelacionTomaRepository
import com.gloria.data.repository.ArticuloTomaRepository
import com.gloria.data.dao.LoggedUserDao
import com.gloria.data.dao.InventarioDetalleDao
import com.gloria.data.dao.AreaDao
import com.gloria.data.dao.DepartamentoDao
import com.gloria.data.dao.SeccionDao
import com.gloria.data.dao.FamiliaDao
import com.gloria.data.dao.GrupoDao
import com.gloria.data.dao.SubgrupoDao
import com.gloria.data.dao.CancelacionTomaDao
import com.gloria.data.dao.ArticuloTomaDao
import com.gloria.data.dao.SucursalDepartamentoDao
import com.gloria.data.repository.ArticuloLoteRepository
import com.gloria.domain.usecase.sincronizacion.SincronizarDatosUseCase
import com.gloria.domain.usecase.sincronizacion.GetEstadisticasSincronizacionUseCase
import com.gloria.domain.usecase.sincronizacion.SincronizarInventariosUseCase
import com.gloria.domain.usecase.sincronizacion.GetTotalInventariosLocalesUseCase
import com.gloria.domain.usecase.inventario.GetInventariosCardsUseCase
import com.gloria.domain.usecase.auth.GetLoggedUserSyncUseCase
import com.gloria.domain.usecase.inventario.GetArticulosInventarioUseCase
import com.gloria.domain.usecase.inventario.ActualizarCantidadInventarioUseCase
import com.gloria.domain.usecase.inventario.ActualizarEstadoInventarioUseCase
import com.gloria.data.repository.InventarioDetalleRepository
import com.gloria.data.repository.InventarioSincronizacionRepository
import com.gloria.domain.usecase.toma.GetLoggedUserUseCase
import com.gloria.domain.usecase.toma.GetSubgruposUseCase
import com.gloria.domain.usecase.exportacion.GetInventariosPendientesExportarUseCase
import com.gloria.domain.usecase.exportacion.ExportarConteosRealizadosUseCase
import com.gloria.domain.usecase.exportacion.ExportarConteosParaVerificacionUseCase
import com.gloria.domain.usecase.enviarconteo.EnviarConteoVerificacionUseCase
import com.gloria.data.repository.ExportacionConteosRepository
import com.gloria.data.repository.EnviarConteoRepository
import com.gloria.data.api.EnviarConteoVerificacionApi
import com.gloria.domain.usecase.conteopendiente.GetConteosPendientesByDateUseCase
import com.gloria.data.repository.ConteoPendienteRepository
import com.gloria.data.api.ConteoPendienteApi
import retrofit2.Retrofit
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    
   @Provides
    @Singleton
    fun provideGetCancelacionesTomaUseCase(
         cancelacionTomaRepository: CancelacionTomaRepository
    ): GetCancelacionesTomaUseCase {
         return GetCancelacionesTomaUseCase(cancelacionTomaRepository)
     }

    @Provides
    @Singleton
    fun provideCancelarTomaParcialUseCase(
        cancelacionTomaRepository: CancelacionTomaRepository
    ): CancelarTomaParcialUseCase {
        return CancelarTomaParcialUseCase(cancelacionTomaRepository)
    }

    @Provides
    @Singleton
    fun provideCancelarTomaTotalUseCase(
        cancelacionTomaRepository: CancelacionTomaRepository
    ): CancelarTomaTotalUseCase {
        return CancelarTomaTotalUseCase(cancelacionTomaRepository)
    }

    @Provides
    @Singleton
    fun provideGetArticulosTomaUseCase(
        articuloTomaRepository: ArticuloTomaRepository
    ): GetArticulosTomaUseCase {
        return GetArticulosTomaUseCase(articuloTomaRepository)
    }

    @Provides
    @Singleton
    fun provideLoginUseCase(
        loggedUserRepository: LoggedUserRepository
    ): LoginUseCase {
        return LoginUseCase(loggedUserRepository)
    }

    @Provides
    @Singleton
    fun provideLogoutUseCase(
        loggedUserRepository: LoggedUserRepository
    ): LogoutUseCase {
        return LogoutUseCase(loggedUserRepository)
    }

    @Provides
    @Singleton
    fun provideRegisterUseCase(
        loggedUserRepository: LoggedUserRepository
    ): RegisterUseCase {
        return RegisterUseCase(loggedUserRepository)
    }

    @Provides
    @Singleton
    fun provideGetSucursalesUseCase(
        loggedUserRepository: LoggedUserRepository
    ): GetSucursalesUseCase {
        return GetSucursalesUseCase(loggedUserRepository)
    }

    @Provides
    @Singleton
    fun provideAuthSessionUseCase(
        authSessionRepository: AuthSessionRepository
    ): AuthSessionUseCase {
        return AuthSessionUseCase(authSessionRepository)
    }

    @Provides
    @Singleton
    fun provideSaveTomaManualUseCase(
        authRepository: AuthRepository,
        inventarioRepository: InventarioRepository,
        sincronizacionRepository: SincronizacionCompletaRepository
    ): SaveTomaManualUseCase {
        return SaveTomaManualUseCase(
            authRepository,
            inventarioRepository,
            sincronizacionRepository
        )
    }

    @Provides
    @Singleton
    fun provideInsertarCabeceraInventarioUseCase(
        loggedUserRepository: LoggedUserRepository,
        authSessionUseCase: AuthSessionUseCase
    ): InsertarCabeceraInventarioUseCase {
        return InsertarCabeceraInventarioUseCase(loggedUserRepository, authSessionUseCase)
    }

    @Provides
    @Singleton
    fun provideInsertarDetalleInventarioUseCase(
        loggedUserRepository: LoggedUserRepository,
        authSessionUseCase: AuthSessionUseCase
    ): InsertarDetalleInventarioUseCase {
        return InsertarDetalleInventarioUseCase(loggedUserRepository, authSessionUseCase)
    }


    @Provides
    @Singleton
    fun provideInsertarCabeceraYDetalleInventarioUseCase(
        repository: ArticuloLoteRepository
    ): InsertarCabeceraYDetalleInventarioUseCase {
        return InsertarCabeceraYDetalleInventarioUseCase(repository)
    }
    @Provides
    @Singleton
    fun provideGetArticulosLotesUseCase(
        repository: ArticuloLoteRepository
    ): GetArticulosLotesUseCase {
        return GetArticulosLotesUseCase(repository)
    }



    @Provides
    @Singleton
    fun provideGetAreasUseCase(
        areaRepository: AreaRepository
    ): GetAreasUseCase {
        return GetAreasUseCase(areaRepository)
    }

    @Provides
    @Singleton
    fun provideGetDepartamentosUseCase(
        departamentoRepository: DepartamentoRepository
    ): GetDepartamentosUseCase {
        return GetDepartamentosUseCase(departamentoRepository)
    }

    @Provides
    @Singleton
    fun provideGetSeccionesUseCase(
        seccionRepository: SeccionRepository
    ): GetSeccionesUseCase {
        return GetSeccionesUseCase(seccionRepository)
    }

    @Provides
    @Singleton
    fun provideGetFamiliasUseCase(
        familiaRepository: FamiliaRepository
    ): GetFamiliasUseCase {
        return GetFamiliasUseCase(familiaRepository)
    }

    @Provides
    @Singleton
    fun provideGetGruposUseCase(
        grupoRepository: GrupoRepository
    ): GetGruposUseCase {
        return GetGruposUseCase(grupoRepository)
    }

    @Provides
    @Singleton
    fun provideGetLoggedUserUseCase(
        loggedUserRepository: LoggedUserRepository
    ): GetLoggedUserUseCase {
        return GetLoggedUserUseCase(loggedUserRepository)
    }



    @Provides
    @Singleton
    fun provideLoggedUserRepository(
        loggedUserDao: LoggedUserDao
    ): LoggedUserRepository {
        return LoggedUserRepository(loggedUserDao)
    }

    @Provides
    @Singleton
    fun provideAuthSessionRepository(
        loggedUserDao: LoggedUserDao
    ): AuthSessionRepository {
        return AuthSessionRepository(loggedUserDao)
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
    fun provideArticuloTomaRepository(
        articuloTomaDao: ArticuloTomaDao
    ): ArticuloTomaRepository {
        return ArticuloTomaRepository(articuloTomaDao)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        loggedUserRepository: LoggedUserRepository
    ): AuthRepository {
        return AuthRepository(loggedUserRepository)
    }

    @Provides
    @Singleton
    fun provideInventarioRepository(
        inventarioDetalleDao: InventarioDetalleDao,
        loggedUserRepository: LoggedUserRepository
    ): InventarioRepository {
        return InventarioRepository(inventarioDetalleDao, loggedUserRepository)
    }

    @Provides
    @Singleton
    fun provideSincronizacionCompletaRepository(
        areaRepository: AreaRepository,
        departamentoRepository: DepartamentoRepository,
        seccionRepository: SeccionRepository,
        familiaRepository: FamiliaRepository,
        grupoRepository: GrupoRepository,
        subgrupoRepository: SubgrupoRepository,
        sucursalDepartamentoRepository: SucursalDepartamentoRepository,
        authSessionUseCase: AuthSessionUseCase
    ): SincronizacionCompletaRepository {
        return SincronizacionCompletaRepository(
            areaRepository, departamentoRepository, seccionRepository, familiaRepository,
            grupoRepository, subgrupoRepository, sucursalDepartamentoRepository, authSessionUseCase
        )
    }

    @Provides
    @Singleton
    fun provideAreaRepository(
        areaDao: AreaDao
    ): AreaRepository {
        return AreaRepository(areaDao)
    }

    @Provides
    @Singleton
    fun provideDepartamentoRepository(
        departamentoDao: DepartamentoDao
    ): DepartamentoRepository {
        return DepartamentoRepository(departamentoDao)
    }

    @Provides
    @Singleton
    fun provideSeccionRepository(
        seccionDao: SeccionDao
    ): SeccionRepository {
        return SeccionRepository(seccionDao)
    }

    @Provides
    @Singleton
    fun provideFamiliaRepository(
        familiaDao: FamiliaDao
    ): FamiliaRepository {
        return FamiliaRepository(familiaDao)
    }

    @Provides
    @Singleton
    fun provideGrupoRepository(
        grupoDao: GrupoDao
    ): GrupoRepository {
        return GrupoRepository(grupoDao)
    }

    @Provides
    @Singleton
    fun provideSubgrupoRepository(
        subgrupoDao: SubgrupoDao
    ): SubgrupoRepository {
        return SubgrupoRepository(subgrupoDao)
    }

    @Provides
    @Singleton
    fun provideSucursalDepartamentoRepository(
        sucursalDepartamentoDao: SucursalDepartamentoDao
    ): SucursalDepartamentoRepository {
        return SucursalDepartamentoRepository(sucursalDepartamentoDao)
    }

    @Provides
    @Singleton
    fun provideGetSubgruposUseCase(
        repository: SubgrupoRepository
    ): GetSubgruposUseCase {
        return GetSubgruposUseCase(repository)
    }
    @Provides
    @Singleton
    fun provideSincronizarDatosUseCase(
        sincronizacionCompletaRepository: SincronizacionCompletaRepository
    ): SincronizarDatosUseCase {
        return SincronizarDatosUseCase(sincronizacionCompletaRepository)
    }

    @Provides
    @Singleton
    fun provideGetEstadisticasSincronizacionUseCase(
        sincronizacionCompletaRepository: SincronizacionCompletaRepository
    ): GetEstadisticasSincronizacionUseCase {
        return GetEstadisticasSincronizacionUseCase(sincronizacionCompletaRepository)
    }

    @Provides
    @Singleton
    fun provideSincronizarInventariosUseCase(
        inventarioSincronizacionRepository: InventarioSincronizacionRepository
    ): SincronizarInventariosUseCase {
        return SincronizarInventariosUseCase(inventarioSincronizacionRepository)
    }

    @Provides
    @Singleton
    fun provideGetTotalInventariosLocalesUseCase(
        inventarioSincronizacionRepository: InventarioSincronizacionRepository
    ): GetTotalInventariosLocalesUseCase {
        return GetTotalInventariosLocalesUseCase(inventarioSincronizacionRepository)
    }


    @Provides
    @Singleton
    fun provideGetInventariosCardsUseCase(
        inventarioDetalleRepository: InventarioDetalleRepository
    ): GetInventariosCardsUseCase {
        return GetInventariosCardsUseCase(inventarioDetalleRepository)
    }

    @Provides
    @Singleton
    fun provideGetLoggedUserSyncUseCase(
        loggedUserRepository: LoggedUserRepository
    ): GetLoggedUserSyncUseCase {
        return GetLoggedUserSyncUseCase(loggedUserRepository)
    }

    @Provides
    @Singleton
    fun provideGetArticulosInventarioUseCase(
        inventarioDetalleRepository: InventarioDetalleRepository
    ): GetArticulosInventarioUseCase {
        return GetArticulosInventarioUseCase(inventarioDetalleRepository)
    }

    @Provides
    @Singleton
    fun provideActualizarCantidadInventarioUseCase(
        inventarioDetalleRepository: InventarioDetalleRepository
    ): ActualizarCantidadInventarioUseCase {
        return ActualizarCantidadInventarioUseCase(inventarioDetalleRepository)
    }

    @Provides
    @Singleton
    fun provideActualizarEstadoInventarioUseCase(
        inventarioDetalleRepository: InventarioDetalleRepository
    ): ActualizarEstadoInventarioUseCase {
        return ActualizarEstadoInventarioUseCase(inventarioDetalleRepository)
    }

    @Provides
    @Singleton
    fun provideInventarioDetalleRepository(
        inventarioDetalleDao: InventarioDetalleDao
    ): InventarioDetalleRepository {
        return InventarioDetalleRepository(inventarioDetalleDao)
    }

    
    // ==================== USECASES DE EXPORTACIÓN ====================
    
    @Provides
    @Singleton
    fun provideGetInventariosPendientesExportarUseCase(
        articuloTomaRepository: ArticuloTomaRepository
    ): GetInventariosPendientesExportarUseCase {
        return GetInventariosPendientesExportarUseCase(articuloTomaRepository)
    }
    

    
    @Provides
    @Singleton
    fun provideExportacionConteosRepository(
        inventarioDetalleDao: InventarioDetalleDao,
        enviarConteoVerificacionUseCase: EnviarConteoVerificacionUseCase,
        loggedUserDao: LoggedUserDao
    ): ExportacionConteosRepository {
        return ExportacionConteosRepository(inventarioDetalleDao, enviarConteoVerificacionUseCase, loggedUserDao)
    }
    
    @Provides
    @Singleton
    fun provideExportarConteosRealizadosUseCase(
        exportacionConteosRepository: ExportacionConteosRepository
    ): ExportarConteosRealizadosUseCase {
        return ExportarConteosRealizadosUseCase(exportacionConteosRepository)
    }
    
    @Provides
    @Singleton
    fun provideExportarConteosParaVerificacionUseCase(
        exportacionConteosRepository: ExportacionConteosRepository
    ): ExportarConteosParaVerificacionUseCase {
        return ExportarConteosParaVerificacionUseCase(exportacionConteosRepository)
    }
    
    // ==================== PROVIDERS PARA ENVÍO DE CONTEO ====================
    
    @Provides
    @Singleton
    fun provideEnviarConteoVerificacionApi(
        @DefaultRetrofit retrofit: Retrofit
    ): EnviarConteoVerificacionApi {
        return retrofit.create(EnviarConteoVerificacionApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideEnviarConteoRepository(
        enviarConteoVerificacionApi: EnviarConteoVerificacionApi
    ): EnviarConteoRepository {
        return EnviarConteoRepository(enviarConteoVerificacionApi)
    }
    
    @Provides
    @Singleton
    fun provideEnviarConteoVerificacionUseCase(
        enviarConteoRepository: EnviarConteoRepository
    ): EnviarConteoVerificacionUseCase {
        return EnviarConteoVerificacionUseCase(enviarConteoRepository)
    }
    
    // ==================== PROVIDERS PARA CONTEOS PENDIENTES ====================
    
    @Provides
    @Singleton
    fun provideConteoPendienteApi(
        @DefaultRetrofit retrofit: Retrofit
    ): ConteoPendienteApi {
        return retrofit.create(ConteoPendienteApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideConteoPendienteRepository(
        conteoPendienteApi: ConteoPendienteApi
    ): ConteoPendienteRepository {
        return ConteoPendienteRepository(conteoPendienteApi)
    }
    
    @Provides
    @Singleton
    fun provideGetConteosPendientesByDateUseCase(
        conteoPendienteRepository: ConteoPendienteRepository
    ): GetConteosPendientesByDateUseCase {
        return GetConteosPendientesByDateUseCase(conteoPendienteRepository)
    }

}