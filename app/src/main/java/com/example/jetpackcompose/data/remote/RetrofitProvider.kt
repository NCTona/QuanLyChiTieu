package com.example.jetpackcompose.data.remote

import android.content.Context
import com.example.jetpackcompose.network.UnsafeOkHttpClient
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    // Khai báo một Builder rỗng (không gắn Interceptor) dùng riêng cho việc Lấy Refresh Token
    // Tránh bị dính vô hạn Interceptor 401
    private fun provideTokenApiService(): ApiService {
        return Retrofit.Builder()
            .baseUrl(BaseURL.baseUrl)
            .client(UnsafeOkHttpClient.createCleanClient()) // Client Sạch
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }

    // Cung cấp API Service chính cho toàn app
    fun provideApiService(context: Context): ApiService {
        val tokenApi = provideTokenApiService()
        val authInterceptor = AuthInterceptor(context, tokenApi)
        val tokenAuthenticator = TokenAuthenticator(context, tokenApi)

        val okHttpClient = UnsafeOkHttpClient.createClientWithAuth(
            authInterceptor,
            tokenAuthenticator
        )

        return Retrofit.Builder()
            .baseUrl(BaseURL.baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}

