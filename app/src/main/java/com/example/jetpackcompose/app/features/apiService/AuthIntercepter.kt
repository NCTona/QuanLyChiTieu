package com.example.jetpackcompose.app.features.apiService

import android.content.Context
import android.util.Log
import com.example.jetpackcompose.app.features.apiService.RefreshAccessTokenAPI.JwtUtils
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Interceptor
import okhttp3.Response

/**
 * AuthInterceptor chịu trách nhiệm:
 * 1. Kiểm tra JWT đã hết hạn chưa TRƯỚC KHI gửi request → proactive refresh
 * 2. Tự động gắn Bearer Token vào mọi request cần auth
 * 3. Xử lý response 403 (fallback khi proactive check bị lệch thời gian)
 */
class AuthInterceptor(
    private val context: Context,
    private val tokenApiService: ApiService
) : Interceptor {

    companion object {
        private const val TAG = "AuthInterceptor"
        private val refreshMutex = Mutex()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val tokenStorage = TokenStorage(context)
        val originalRequest = chain.request()

        // Bỏ qua Token cho các request không cần đăng nhập
        val noAuthRequired = listOf(
            "/api/users/register",
            "/api/users/login",
            "/api/users/refresh-token",
            "/api/users/send-otp",
            "/api/users/verify-otp",
            "/api/users/reset-password"
        )

        val urlPath = originalRequest.url().encodedPath()
        if (noAuthRequired.any { urlPath.contains(it) }) {
            return chain.proceed(originalRequest)
        }

        // === PROACTIVE REFRESH ===
        // Kiểm tra token đã hết hạn TRƯỚC KHI gửi request
        var accessToken = tokenStorage.getAccessToken()
        if (accessToken != null && isTokenExpired(accessToken)) {
            Log.d(TAG, "Token đã hết hạn, proactive refresh trước khi gửi request...")
            val refreshedToken = tryRefreshToken(tokenStorage)
            if (refreshedToken != null) {
                accessToken = refreshedToken
                Log.d(TAG, "Proactive refresh thành công!")
            } else {
                Log.e(TAG, "Proactive refresh thất bại")
            }
        }

        // Gắn Access Token vào Request
        val authenticatedRequest = if (accessToken != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        } else {
            originalRequest
        }

        val response = chain.proceed(authenticatedRequest)

        // === FALLBACK: Xử lý 403 ===
        // Phòng trường hợp proactive check bị lệch hoặc server trả 403 vì lý do khác
        if (response.code() == 403) {
            Log.d(TAG, "Nhận 403, thử refresh token...")
            val newAccessToken = tryRefreshToken(tokenStorage)

            if (newAccessToken != null) {
                Log.d(TAG, "Refresh thành công, retry request...")
                response.close()

                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()
                return chain.proceed(newRequest)
            }
        }

        return response
    }

    /**
     * Kiểm tra JWT đã hết hạn chưa (với buffer 10 giây để tránh race condition)
     */
    private fun isTokenExpired(token: String): Boolean {
        val expireTimeMillis = JwtUtils.getExpireTimeMillis(token)
        if (expireTimeMillis == 0L) return false // Không parse được → coi như chưa hết hạn
        // Trừ buffer 10 giây để refresh sớm hơn 1 chút
        return System.currentTimeMillis() >= (expireTimeMillis - 10_000)
    }

    /**
     * Thử refresh token an toàn đa luồng.
     */
    private fun tryRefreshToken(tokenStorage: TokenStorage): String? {
        var newToken: String? = null

        runBlocking {
            refreshMutex.withLock {
                // Kiểm tra lại: có thể luồng khác đã refresh xong
                val currentToken = tokenStorage.getAccessToken()
                if (currentToken != null && !isTokenExpired(currentToken)) {
                    newToken = currentToken
                    return@withLock
                }

                val refreshToken = tokenStorage.getRefreshToken()
                if (refreshToken != null) {
                    try {
                        val refreshResponse = tokenApiService.refreshToken(RefreshToken(refreshToken))
                        if (refreshResponse.isSuccessful) {
                            refreshResponse.body()?.let {
                                tokenStorage.saveTokens(it.accessToken, it.refreshToken)
                                newToken = it.accessToken
                                Log.d(TAG, "Token refreshed OK")
                            }
                        } else {
                            Log.e(TAG, "Refresh thất bại: ${refreshResponse.code()}")
                            tokenStorage.clear()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Lỗi refresh: ${e.message}")
                    }
                }
            }
        }

        return newToken
    }
}
