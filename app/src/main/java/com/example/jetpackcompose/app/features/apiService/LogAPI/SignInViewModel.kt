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
import com.example.jetpackcompose.app.features.apiService.LoginData
import com.example.jetpackcompose.app.features.apiService.LoginResponse
import com.example.jetpackcompose.app.features.apiService.RefreshAccessTokenAPI.RefreshTokenScheduler
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignInViewModel(private val context: Context) : ViewModel() {

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val api: ApiService = Retrofit.Builder()
        .baseUrl(BaseURL.baseUrl)
        .addConverterFactory(GsonConverterFactory.create(gson))
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

    var loginStatus: String = ""
        private set

    // Hàm lưu cả access + refresh
    private fun saveTokens(accessToken: String, refreshToken: String) {
        sharedPreferences.edit()
            .putString("is_logged_in", true.toString())
            .putString("auth_token", accessToken)
            .putString("refresh_token", refreshToken)
            .apply()
    }

    // Lấy access token (giữ nguyên)
    fun getToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }


    fun signInUser(
        data: LoginData,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = api.login(data)

                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody == null) {
                        loginStatus = "Login failed: Empty response from server"
                        onError(loginStatus)
                        return@launch
                    }

                    if (responseBody.status == "success") {
                        val accessToken = responseBody.accessToken
                        val refreshToken = responseBody.refreshToken

                        if (!accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty()) {
                            saveTokens(accessToken, refreshToken)
                            RefreshTokenScheduler.schedule(context)
                            loginStatus = "Login successful"
                            onSuccess(loginStatus)
                        } else {
                            loginStatus = "Login failed: Invalid token"
                            onError(loginStatus)
                        }
                    } else {
                        loginStatus = "${responseBody.message}"
                        onError(loginStatus)
                    }

                } else {
                    // Parse errorBody từ BE
                    val errorBodyString = response.errorBody()?.string()

                    if (errorBodyString != null) {
                        val errorResponse =
                            gson.fromJson(errorBodyString, LoginResponse::class.java)
                        loginStatus = errorResponse.message ?: "Login failed"
                        onError(loginStatus)
                    } else {
                        loginStatus = "Login failed: Unknown server error"
                        onError(loginStatus)
                    }
                }

            } catch (e: JsonSyntaxException) {
                loginStatus = "JSON syntax error"
                Log.e("SignInViewModel", "JSON Syntax Error", e)
                onError(loginStatus)

            } catch (e: JsonParseException) {
                loginStatus = "JSON parse error"
                Log.e("SignInViewModel", "JSON Parse Error", e)
                onError(loginStatus)

            } catch (e: Exception) {
                loginStatus = "General error: ${e.localizedMessage}"
                Log.e("SignInViewModel", "General Error", e)
                onError(loginStatus)
            }
        }
    }
}
