package com.example.jetpackcompose.app.features.apiService

import android.content.Context
import com.example.jetpackcompose.app.features.apiService.RefreshAccessTokenAPI.TokenRefreshLock
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.withLock
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthInterceptor(
    private val context: Context,
    private val apiService: ApiService
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val tokenStorage = TokenStorage(context)

        runBlocking {
            TokenRefreshLock.mutex.withLock {

                if (tokenStorage.isAccessTokenExpired(context)) {
                    val refreshToken = tokenStorage.getRefreshToken()
                        ?: throw IOException("Refresh token missing")

                    val response = apiService.refreshToken(
                        RefreshToken(refreshToken)
                    )

                    if (response.isSuccessful) {
                        response.body()?.let {
                            tokenStorage.saveTokens(
                                it.accessToken,
                                it.refreshToken
                            )
                        }
                    } else {
                        tokenStorage.clear()
                        throw IOException("Session expired")
                    }
                }
            }
        }

        val newRequest = chain.request().newBuilder()
            .addHeader(
                "Authorization",
                "Bearer ${tokenStorage.getAccessToken()}"
            )
            .build()

        return chain.proceed(newRequest)
    }
}
