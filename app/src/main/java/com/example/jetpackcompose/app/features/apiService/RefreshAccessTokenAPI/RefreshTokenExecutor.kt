package com.example.jetpackcompose.app.features.apiService.RefreshAccessTokenAPI

import android.content.Context
import com.example.jetpackcompose.app.features.apiService.TokenStorage
import com.example.jetpackcompose.app.features.apiService.ApiService
import com.example.jetpackcompose.app.features.apiService.RefreshToken

class RefreshTokenExecutor(
    private val context: Context,
    private val apiService: ApiService
) {

    private val tokenStorage = TokenStorage(context)

    suspend fun refresh() {
        val refreshTokenValue = tokenStorage.getRefreshToken() ?: return

        // Bọc refresh token vào data class
        val requestBody = RefreshToken(refreshTokenValue)

        val response = apiService.refreshToken(requestBody)

        if (response.isSuccessful) {
            response.body()?.let {
                tokenStorage.saveTokens(
                    it.accessToken,
                    it.refreshToken
                )

                // Lập lịch lại với token mới
                RefreshTokenScheduler.schedule(context)
            }
        }
    }
}

