package com.gloria.di

 import android.app.Application
import android.content.Context
import androidx.annotation.Keep
 import com.gloria.BuildConfig
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
        val timeoutValue = 40L
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(timeoutValue, TimeUnit.SECONDS)
                    .readTimeout(timeoutValue, TimeUnit.SECONDS)
                    .writeTimeout(timeoutValue, TimeUnit.SECONDS)
                    .build()
            )
            .build()
    }

}
