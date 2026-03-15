package com.example.jetpackcompose.data.remote

import com.example.jetpackcompose.data.remote.dto.RefreshToken
import android.content.Context
import com.example.jetpackcompose.data.local.TokenStorage
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.io.IOException

class TokenAuthenticator(
    private val context: Context,
    // Chúng ta dùng một Instance ApiService "sạch" riêng biệt cho Refresh 
    // Tránh bị dính vô hạn Interceptor
    private val tokenApiService: ApiService 
) : Authenticator {

    companion object {
        private val mutex = Mutex()
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        val tokenStorage = TokenStorage(context)
        var newAccessToken: String? = null

        // Chặn luồng Coroutines để bắt đồng bộ 1 luồng refresh duy nhất
        runBlocking {
            mutex.withLock {
                // Kiểm tra xem luồng khác đã refresh xong token chưa?
                val currentAccessToken = tokenStorage.getAccessToken()
                if (currentAccessToken != null && response.request().header("Authorization") != "Bearer $currentAccessToken") {
                     // Nếu token trong storage đã mới hơn token trong Request bị lỗi -> dùng luôn
                     newAccessToken = currentAccessToken
                } else {
                     // Tiến hành gọi lấy Token Mới
                     val refreshToken = tokenStorage.getRefreshToken()
                     if (refreshToken != null) {
                         try {
                              val refreshResponse = tokenApiService.refreshToken(RefreshToken(refreshToken))
                              if (refreshResponse.isSuccessful) {
                                  refreshResponse.body()?.let {
                                      tokenStorage.saveTokens(it.accessToken, it.refreshToken)
                                      newAccessToken = it.accessToken
                                  }
                              } else {
                                  tokenStorage.clear() // Refresh hết hạn -> Đăng xuất
                              }
                         } catch (e: Exception) {
                              e.printStackTrace()
                         }
                     }
                }
            }
        }

        // Trả về Request cấu hình lại với Token mới để OkHttp thực thi tiếp
        return if (newAccessToken != null) {
            response.request().newBuilder()
                .header("Authorization", "Bearer $newAccessToken")
                .build()
        } else {
            null // Không cứu được, văng lỗi 401 ra UI
        }
    }
}

