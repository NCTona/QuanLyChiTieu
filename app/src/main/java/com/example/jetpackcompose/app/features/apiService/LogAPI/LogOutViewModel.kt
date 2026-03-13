package com.example.jetpackcompose.app.features.apiService.LogAPI

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.jetpackcompose.app.features.apiService.ApiService
import com.example.jetpackcompose.app.features.apiService.BaseURL
import com.example.jetpackcompose.app.features.apiService.RefreshAccessTokenAPI.RefreshTokenScheduler
import com.example.jetpackcompose.network.UnsafeOkHttpClient
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LogOutViewModel(private val context: Context) : ViewModel() {

    private val api: ApiService = Retrofit.Builder()
        .baseUrl(BaseURL.baseUrl)
        .client(UnsafeOkHttpClient.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences =
        EncryptedSharedPreferences.create(
            context,
            "secure_user_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    var logoutStatus: String = ""
        private set

    // Lấy access token từ shared prefs
    private fun getToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }

    // Xóa token sau khi logout thành công
    private fun clearTokens() {
        sharedPreferences.edit()
            .remove("is_logged_in")
            .remove("auth_token")
            .remove("refresh_token")
            .apply()
    }

    fun logOutUser(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val token = getToken()
                if (token.isNullOrEmpty()) {
                    logoutStatus = "No token found"
                    onError(logoutStatus)
                    return@launch
                }

                // Gọi API logout với token trong header
                val response = api.logout("Bearer $token")

                if (response.isSuccessful) {
                    clearTokens()
                    logoutStatus = "Logout successful"
                    onSuccess(logoutStatus)
                } else {
                    logoutStatus = "Logout failed: ${response.code()}"
                    onError(logoutStatus)
                }

            } catch (e: Exception) {
                logoutStatus = "Logout error: ${e.localizedMessage}"
                Log.e("LogOutViewModel", "General Error", e)
                onError(logoutStatus)
            }
        }
    }
}
