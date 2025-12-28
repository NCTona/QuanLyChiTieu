package com.example.jetpackcompose.app.features.apiService

import com.example.jetpackcompose.app.features.apiService.ApiService
import com.example.jetpackcompose.app.features.apiService.BaseURL
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    fun provideApiService(): ApiService {
        return Retrofit.Builder()
            .baseUrl(BaseURL.baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}
