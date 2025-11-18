package com.gloria.di

import com.gloria.domain.usecase.GetSucursalesUseCase
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
import com.gloria.domain.usecase.permission.CheckUserPermissionUseCase
import com.gloria.domain.usecase.permission.SyncUserPermissionsUseCase
import com.gloria.domain.usecase.permission.GetUserAllowedScreensUseCase
import com.gloria.domain.usecase.permission.SyncUserPermissionsFromOracleUseCase
import com.gloria.domain.usecase.toma.GetDepartamentosUseCase
import com.gloria.domain.usecase.toma.GetSeccionesUseCase
import com.gloria.domain.usecase.toma.GetFamiliasUseCase
import com.gloria.domain.usecase.toma.GetGruposUseCase
import com.gloria.domain.usecase.toma.GetSucursalesUseCase as GetSucursalesTomaUseCase
 import com.gloria.repository.AuthRepository
import com.gloria.repository.InventarioRepository
import com.gloria.repository.SincronizacionCompletaRepository
import com.gloria.data.repository.DatosMaestrosApiRepository
import com.gloria.data.repository.InsertarCabeceraYDetalleApiRepository
import com.gloria.data.repository.UserPermissionsApiRepository
import com.gloria.data.repository.OracleLoginApiRepository
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
import com.gloria.data.repository.ArticulosClasificacionApiRepository
import com.gloria.domain.usecase.sincronizacion.SincronizarDatosUseCase
import com.gloria.domain.usecase.sincronizacion.GetEstadisticasSincronizacionUseCase
import com.gloria.domain.usecase.sincronizacion.SincronizarInventariosUseCase
import com.gloria.domain.usecase.sincronizacion.GetTotalInventariosLocalesUseCase
import com.gloria.domain.usecase.inventario.GetInventariosCardsUseCase
import com.gloria.domain.usecase.auth.GetLoggedUserSyncUseCase
import com.gloria.domain.usecase.inventario.GetArticulosInventarioUseCase
import com.gloria.domain.usecase.inventario.ActualizarCantidadInventarioUseCase
import com.gloria.domain.usecase.inventario.ActualizarCantidadInventarioSoloCantidadUseCase
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
import com.gloria.data.repository.ConteosLogsRepository
import com.gloria.data.repository.ConteosLogsApiRepository
import com.gloria.data.repository.ConteosLogsConsultaRepository
import com.gloria.data.api.ConteoPendienteApi
import com.gloria.data.dao.UserPermissionOracleDao
import com.gloria.data.dao.UserPermissionDao
import com.gloria.data.dao.ConteosLogsDao
import com.gloria.data.repository.ArticuloLoteRepository
import com.gloria.data.repository.UserPermissionRepository
import com.gloria.data.repository.InventariosPendientesSimultaneosRepository
import com.gloria.domain.usecase.auth.LoginUseCase
import com.gloria.domain.usecase.auth.LogoutUseCase
import com.gloria.domain.usecase.auth.RegisterUseCase
import com.gloria.domain.usecase.permission.LoginWithPermissionSyncUseCase
import com.gloria.domain.usecase.inventario.GetInventariosPendientesSimultaneosUseCase
import com.gloria.domain.usecase.inventario.ConfirmarConteoSimultaneoUseCase
import com.gloria.domain.usecase.inventario.ConteosLogsUseCase
import com.gloria.domain.usecase.inventario.ActualizarUsuarioCerradoInventarioUseCase
import com.gloria.domain.usecase.inventario.EnviarConteosLogsUseCase
import com.gloria.data.repository.ConfirmarConteoSimultaneoRepository
import com.gloria.di.DefaultRetrofit
import com.gloria.domain.usecase.inventario.GetConteosLogsRemotosUseCase
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
        loggedUserRepository: LoggedUserRepository,
        oracleLoginApiRepository: OracleLoginApiRepository
    ): LoginUseCase {
        return LoginUseCase(loggedUserRepository, oracleLoginApiRepository)
    }

    @Provides
    @Singleton
    fun provideLoginWithPermissionSyncUseCase(
        loginUseCase: LoginUseCase,
        syncUserPermissionsFromOracleUseCase: SyncUserPermissionsFromOracleUseCase
    ): LoginWithPermissionSyncUseCase {
        return LoginWithPermissionSyncUseCase(loginUseCase, syncUserPermissionsFromOracleUseCase)
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
    fun provideRegisterUseCase(): RegisterUseCase {
        return RegisterUseCase()
    }

    @Provides
    @Singleton
    fun provideGetSucursalesUseCase(
        loggedUserRepository: LoggedUserRepository,
        oracleLoginApiRepository: OracleLoginApiRepository
    ): GetSucursalesUseCase {
        return GetSucursalesUseCase(loggedUserRepository, oracleLoginApiRepository)
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
        loggedUserRepository: LoggedUserRepository,
        inventarioRepository: InventarioRepository,
        sincronizacionRepository: SincronizacionCompletaRepository,
        insertarCabeceraYDetalleInventarioUseCase: InsertarCabeceraYDetalleInventarioUseCase
    ): SaveTomaManualUseCase {
        return SaveTomaManualUseCase(
            loggedUserRepository,
            inventarioRepository,
            sincronizacionRepository,
            insertarCabeceraYDetalleInventarioUseCase
        )
    }

    @Provides
    @Singleton
    fun provideInsertarCabeceraInventarioUseCase(
        loggedUserRepository: LoggedUserRepository,
        articuloLoteRepository: ArticuloLoteRepository
    ): InsertarCabeceraInventarioUseCase {
        return InsertarCabeceraInventarioUseCase(loggedUserRepository, articuloLoteRepository)
    }

    @Provides
    @Singleton
    fun provideInsertarDetalleInventarioUseCase(
        loggedUserRepository: LoggedUserRepository,
        articuloLoteRepository: ArticuloLoteRepository
    ): InsertarDetalleInventarioUseCase {
        return InsertarDetalleInventarioUseCase(loggedUserRepository, articuloLoteRepository)
    }


    @Provides
    @Singleton
    fun provideInsertarCabeceraYDetalleInventarioUseCase(
        insertarCabeceraYDetalleApiRepository: InsertarCabeceraYDetalleApiRepository,
        loggedUserRepository: LoggedUserRepository
    ): InsertarCabeceraYDetalleInventarioUseCase {
        return InsertarCabeceraYDetalleInventarioUseCase(insertarCabeceraYDetalleApiRepository, loggedUserRepository)
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
        authSessionUseCase: AuthSessionUseCase,
        syncUserPermissionsFromOracleUseCase: SyncUserPermissionsFromOracleUseCase,
        datosMaestrosApiRepository: DatosMaestrosApiRepository
    ): SincronizacionCompletaRepository {
        return SincronizacionCompletaRepository(
            areaRepository, departamentoRepository, seccionRepository, familiaRepository,
            grupoRepository, subgrupoRepository, sucursalDepartamentoRepository, authSessionUseCase,
            syncUserPermissionsFromOracleUseCase, datosMaestrosApiRepository
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
    fun provideActualizarCantidadInventarioSoloCantidadUseCase(
        inventarioDetalleRepository: InventarioDetalleRepository
    ): ActualizarCantidadInventarioSoloCantidadUseCase {
        return ActualizarCantidadInventarioSoloCantidadUseCase(inventarioDetalleRepository)
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
    fun provideActualizarUsuarioCerradoInventarioUseCase(
        inventarioDetalleRepository: InventarioDetalleRepository
    ): ActualizarUsuarioCerradoInventarioUseCase {
        return ActualizarUsuarioCerradoInventarioUseCase(inventarioDetalleRepository)
    }

    @Provides
    @Singleton
    fun provideConteosLogsUseCase(
        conteosLogsRepository: ConteosLogsRepository
    ): ConteosLogsUseCase {
        return ConteosLogsUseCase(conteosLogsRepository)
    }

    @Provides
    @Singleton
    fun provideGetConteosLogsRemotosUseCase(
        conteosLogsConsultaRepository: ConteosLogsConsultaRepository
    ): GetConteosLogsRemotosUseCase {
        return GetConteosLogsRemotosUseCase(conteosLogsConsultaRepository)
    }

    @Provides
    @Singleton
    fun provideEnviarConteosLogsUseCase(
        conteosLogsUseCase: ConteosLogsUseCase,
        conteosLogsApiRepository: ConteosLogsApiRepository
    ): EnviarConteosLogsUseCase {
        return EnviarConteosLogsUseCase(conteosLogsUseCase, conteosLogsApiRepository)
    }

    @Provides
    @Singleton
    fun provideInventarioDetalleRepository(
        inventarioDetalleDao: InventarioDetalleDao
    ): InventarioDetalleRepository {
        return InventarioDetalleRepository(inventarioDetalleDao)
    }

    @Provides
    @Singleton
    fun provideConteosLogsRepository(
        conteosLogsDao: ConteosLogsDao
    ): ConteosLogsRepository {
        return ConteosLogsRepository(conteosLogsDao)
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
    fun provideUserPermissionRepository(
        userPermissionDao: UserPermissionDao
    ): UserPermissionRepository {
        return UserPermissionRepository(userPermissionDao)
    }
    
    @Provides
    @Singleton
    fun provideGetConteosPendientesByDateUseCase(
        conteoPendienteRepository: ConteoPendienteRepository
    ): GetConteosPendientesByDateUseCase {
        return GetConteosPendientesByDateUseCase(conteoPendienteRepository)
    }

    @Provides
    @Singleton
    fun provideCheckUserPermissionUseCase(
        userPermissionRepository: UserPermissionRepository
    ): CheckUserPermissionUseCase {
        return CheckUserPermissionUseCase(userPermissionRepository)
    }

    @Provides
    @Singleton
    fun provideSyncUserPermissionsUseCase(
        userPermissionRepository: UserPermissionRepository
    ): SyncUserPermissionsUseCase {
        return SyncUserPermissionsUseCase(userPermissionRepository)
    }

    @Provides
    @Singleton
    fun provideGetUserAllowedScreensUseCase(
        userPermissionRepository: UserPermissionRepository
    ): GetUserAllowedScreensUseCase {
        return GetUserAllowedScreensUseCase(userPermissionRepository)
    }

    @Provides
    @Singleton
    fun provideSyncUserPermissionsFromOracleUseCase(
        oracleLoginApiRepository: OracleLoginApiRepository,
        userPermissionRepository: UserPermissionRepository
    ): SyncUserPermissionsFromOracleUseCase {
        return SyncUserPermissionsFromOracleUseCase(oracleLoginApiRepository, userPermissionRepository)
    }

    // ==================== PROVIDERS PARA INVENTARIOS PENDIENTES SIMULTÁNEOS ====================
    
    @Provides
    @Singleton
    fun provideInventariosPendientesSimultaneosApiService(
        @DefaultRetrofit retrofit: Retrofit
    ): com.gloria.data.api.InventariosPendientesSimultaneosApiService {
        return retrofit.create(com.gloria.data.api.InventariosPendientesSimultaneosApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideInventariosPendientesSimultaneosRepository(
        apiService: com.gloria.data.api.InventariosPendientesSimultaneosApiService,
        loggedUserRepository: LoggedUserRepository
    ): InventariosPendientesSimultaneosRepository {
        return InventariosPendientesSimultaneosRepository(apiService, loggedUserRepository)
    }
    
    @Provides
    @Singleton
    fun provideGetInventariosPendientesSimultaneosUseCase(
        inventariosPendientesSimultaneosRepository: InventariosPendientesSimultaneosRepository
    ): GetInventariosPendientesSimultaneosUseCase {
        return GetInventariosPendientesSimultaneosUseCase(inventariosPendientesSimultaneosRepository)
    }

    // ==================== PROVIDERS PARA CONFIRMAR CONTEO SIMULTÁNEO ====================
    
    @Provides
    @Singleton
    fun provideConfirmarConteoSimultaneoApiService(
        @DefaultRetrofit retrofit: Retrofit
    ): com.gloria.data.api.ConfirmarConteoSimultaneoApiService {
        return retrofit.create(com.gloria.data.api.ConfirmarConteoSimultaneoApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideConfirmarConteoSimultaneoRepository(
        apiService: com.gloria.data.api.ConfirmarConteoSimultaneoApiService,
        loggedUserRepository: LoggedUserRepository
    ): ConfirmarConteoSimultaneoRepository {
        return ConfirmarConteoSimultaneoRepository(apiService, loggedUserRepository)
    }
    
    @Provides
    @Singleton
    fun provideConfirmarConteoSimultaneoUseCase(
        confirmarConteoSimultaneoRepository: ConfirmarConteoSimultaneoRepository
    ): ConfirmarConteoSimultaneoUseCase {
        return ConfirmarConteoSimultaneoUseCase(confirmarConteoSimultaneoRepository)
    }

}