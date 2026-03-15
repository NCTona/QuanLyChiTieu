package com.example.jetpackcompose.presentation.auth

import com.example.jetpackcompose.data.remote.dto.RefreshToken
import com.example.jetpackcompose.data.remote.ApiService
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.data.local.TokenStorage
import com.example.jetpackcompose.data.remote.dto.LoginData
import com.example.jetpackcompose.data.remote.dto.LoginResponse
import com.example.jetpackcompose.di.AuthApi
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.launch

@HiltViewModel
class SignInViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    @AuthApi private val authApi: ApiService,
    private val tokenStorage: TokenStorage
) : ViewModel() {

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    var loginStatus: String = ""
        private set

    fun getToken(): String? {
        return tokenStorage.getAccessToken()
    }

    fun signInUser(
        data: LoginData,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = authApi.login(data)

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
                            tokenStorage.saveTokens(accessToken, refreshToken)
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
