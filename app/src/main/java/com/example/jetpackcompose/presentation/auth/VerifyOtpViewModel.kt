package com.example.jetpackcompose.presentation.auth

import com.example.jetpackcompose.data.remote.ApiService
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.data.remote.dto.VerifyOtp
import com.example.jetpackcompose.di.AuthApi
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.launch

@HiltViewModel
class VerifyOtpViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    @AuthApi private val authApi: ApiService
) : ViewModel() {
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private fun saveToken(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString("reset_token", token)
        editor.apply()
    }

    var otpStatus: String = ""
        private set

    fun verifyOtp(
        data: VerifyOtp,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = authApi.verifyOtp(data)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        otpStatus = "OTP verified successfully: ${responseBody.message}"
                        saveToken(responseBody.resetToken)
                        onSuccess(otpStatus)
                    } else {
                        otpStatus = "Empty response from server"
                        onError(otpStatus)
                    }
                } else {
                    val errorBodyString = response.errorBody()?.string()
                    if (errorBodyString != null) {
                        try {
                            val errorResponse = gson.fromJson(errorBodyString, VerifyOtp::class.java)
                            val errorMessage = errorResponse.otp
                            otpStatus = "OTP verification failed: $errorMessage"
                            onError(otpStatus)
                        } catch (e: JsonSyntaxException) {
                            otpStatus = "Error parsing error response: ${e.localizedMessage}"
                            onError(otpStatus)
                        } catch (e: JsonParseException) {
                            otpStatus = "Error parsing error response: ${e.localizedMessage}"
                            onError(otpStatus)
                        }
                    } else {
                        otpStatus = "Error parsing error response: ${response.message()}"
                        onError(otpStatus)
                    }
                }
            } catch (e: Exception) {
                otpStatus = "Error: ${e.localizedMessage}"
                onError(otpStatus)
            }
        }
    }
}
