package com.example.jetpackcompose.di

import android.content.Context
import com.example.jetpackcompose.data.remote.ApiService
import com.example.jetpackcompose.data.remote.BaseURL
import com.example.jetpackcompose.data.remote.RetrofitProvider
import com.example.jetpackcompose.network.UnsafeOkHttpClient
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApiService(@ApplicationContext context: Context): ApiService {
        return RetrofitProvider.provideApiService(context)
    }

    /**
     * ApiService KHONG co AuthInterceptor.
     * Dung cho Login, SignUp, ForgotPassword — chua co token.
     */
    @Provides
    @Singleton
    @AuthApi
    fun provideAuthApiService(): ApiService {
        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl(BaseURL.baseUrl)
            .client(UnsafeOkHttpClient.createCleanClient())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}
