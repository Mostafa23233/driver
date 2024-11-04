package com.darbaast.driver.di

import android.content.Context
import com.darbaast.driver.custom.DarbastToast
import com.darbaast.driver.custom.annotation.AppVersion
import com.darbaast.driver.data.sharedPreferences.DarbastSharedPreference
import com.darbaast.driver.room.AppDatabase
import com.darbaast.driver.server.retrofit.ApiService
import com.darbaast.driver.utils.DarbastAnimations
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

const val base_url = "https://darbaast.web-tool.cloud/api/"
@Module
@InstallIn(SingletonComponent::class)
class DarbastModules {

    @Provides
    @Singleton
    fun provideDarbastSharedPreference(@ApplicationContext context: Context) = DarbastSharedPreference(context)


    @Provides
    @Singleton
fun provideHttpLoginInterceptor(): HttpLoggingInterceptor =
    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)



    @Provides
    @Singleton
    fun provideOkHttpClient(appDatabase: AppDatabase,httpLoggingInterceptor: HttpLoggingInterceptor,@AppVersion appVersion: String):OkHttpClient{
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .readTimeout(60,TimeUnit.SECONDS)
            .connectTimeout(60,TimeUnit.SECONDS)
            .addInterceptor {
                val token = runBlocking{appDatabase.tokenDao.getAllToken()}
                val oldRequest = it.request()
                val newRequest = oldRequest.newBuilder()
                if (token.isNotEmpty()){
                    newRequest.addHeader("Authorization","Bearer ${token[0].token}")
                }
                newRequest.addHeader("Accept","application/json")
                newRequest.addHeader("Content-Type","application/json")
                newRequest.addHeader("app-name","driver")
                newRequest.addHeader("app-version",appVersion)
                newRequest.method(oldRequest.method,oldRequest.body)
                return@addInterceptor it.proceed(newRequest.build())
            }
            .build()
    }


    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient):Retrofit{
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(base_url)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun providesApiService(retrofit: Retrofit):ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context) = context

    @Provides
    @Singleton
    fun provideDarbastToast() = DarbastToast()

    @Provides
    @Singleton
    fun provideDarbastAnimation() = DarbastAnimations()

    @Provides
    @AppVersion
    fun provideAppVersion(@ApplicationContext context: Context):String {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName,0)
        return packageInfo.versionName
    }

}