package com.gloria.di

import android.app.Application
import android.content.Context
import androidx.annotation.Keep
import com.gloria.BuildConfig
import com.gloria.data.repository.ArticulosClasificacionApiRepository
import com.gloria.data.repository.DatosMaestrosApiRepository
import com.gloria.data.repository.InsertarCabeceraYDetalleApiRepository
import com.gloria.data.repository.InsertarConteosApiRepository
import com.gloria.data.repository.InventariosPorSucursalApiRepository
import com.gloria.data.repository.InventariosPendientesPorUsuarioApiRepository
import com.gloria.data.repository.ProductosInventarioPorNumeroApiRepository
import com.gloria.data.repository.CancelarTomaApiRepository
import com.gloria.data.repository.OracleLoginApiRepository
import com.gloria.data.repository.UserPermissionsApiRepository
import com.gloria.data.api.ArticulosClasificacionApiService
import com.gloria.data.api.DatosMaestrosApiService
import com.gloria.data.api.InsertarCabeceraYDetalleApiService
import com.gloria.data.api.InsertarConteosApiService
import com.gloria.data.api.InventariosPorSucursalApiService
import com.gloria.data.api.InventariosPendientesPorUsuarioApiService
import com.gloria.data.api.ProductosInventarioPorNumeroApiService
import com.gloria.data.api.CancelarTomaApiService
import com.gloria.data.api.OracleLoginApiService
import com.gloria.data.api.UserPermissionsApiService
 import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocalRetrofit

@Keep
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @DefaultRetrofit
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        val timeoutValue = 180L
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(timeoutValue, TimeUnit.SECONDS)
                    .readTimeout(timeoutValue, TimeUnit.SECONDS)
                    .writeTimeout(timeoutValue, TimeUnit.SECONDS)
                    .callTimeout(timeoutValue, TimeUnit.SECONDS)
                    .build()
            )
            .build()
    }

    @Singleton
    @Provides
    fun provideDatosMaestrosApiService(@DefaultRetrofit retrofit: Retrofit): DatosMaestrosApiService {
        return retrofit.create(DatosMaestrosApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideDatosMaestrosApiRepository(apiService: DatosMaestrosApiService): DatosMaestrosApiRepository {
        return DatosMaestrosApiRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideUserPermissionsApiService(@DefaultRetrofit retrofit: Retrofit): UserPermissionsApiService {
        return retrofit.create(UserPermissionsApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideUserPermissionsApiRepository(apiService: UserPermissionsApiService): UserPermissionsApiRepository {
        return UserPermissionsApiRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideOracleLoginApiService(@DefaultRetrofit retrofit: Retrofit): OracleLoginApiService {
        return retrofit.create(OracleLoginApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideOracleLoginApiRepository(apiService: OracleLoginApiService): OracleLoginApiRepository {
        return OracleLoginApiRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideArticulosClasificacionApiService(@DefaultRetrofit retrofit: Retrofit): ArticulosClasificacionApiService {
        return retrofit.create(ArticulosClasificacionApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideArticulosClasificacionApiRepository(apiService: ArticulosClasificacionApiService): ArticulosClasificacionApiRepository {
        return ArticulosClasificacionApiRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideInsertarCabeceraYDetalleApiService(@DefaultRetrofit retrofit: Retrofit): InsertarCabeceraYDetalleApiService {
        return retrofit.create(InsertarCabeceraYDetalleApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideInsertarCabeceraYDetalleApiRepository(apiService: InsertarCabeceraYDetalleApiService): InsertarCabeceraYDetalleApiRepository {
        return InsertarCabeceraYDetalleApiRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideInventariosPorSucursalApiService(@DefaultRetrofit retrofit: Retrofit): InventariosPorSucursalApiService {
        return retrofit.create(InventariosPorSucursalApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideInventariosPorSucursalApiRepository(apiService: InventariosPorSucursalApiService): InventariosPorSucursalApiRepository {
        return InventariosPorSucursalApiRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideInventariosPendientesPorUsuarioApiService(@DefaultRetrofit retrofit: Retrofit): InventariosPendientesPorUsuarioApiService {
        return retrofit.create(InventariosPendientesPorUsuarioApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideInventariosPendientesPorUsuarioApiRepository(apiService: InventariosPendientesPorUsuarioApiService): InventariosPendientesPorUsuarioApiRepository {
        return InventariosPendientesPorUsuarioApiRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideProductosInventarioPorNumeroApiService(@DefaultRetrofit retrofit: Retrofit): ProductosInventarioPorNumeroApiService {
        return retrofit.create(ProductosInventarioPorNumeroApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideProductosInventarioPorNumeroApiRepository(apiService: ProductosInventarioPorNumeroApiService): ProductosInventarioPorNumeroApiRepository {
        return ProductosInventarioPorNumeroApiRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideInsertarConteosApiService(@DefaultRetrofit retrofit: Retrofit): InsertarConteosApiService {
        return retrofit.create(InsertarConteosApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideInsertarConteosApiRepository(apiService: InsertarConteosApiService): InsertarConteosApiRepository {
        return InsertarConteosApiRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideCancelarTomaApiService(@DefaultRetrofit retrofit: Retrofit): CancelarTomaApiService {
        return retrofit.create(CancelarTomaApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideCancelarTomaApiRepository(apiService: CancelarTomaApiService): CancelarTomaApiRepository {
        return CancelarTomaApiRepository(apiService)
    }

}
