package com.example.jetpackcompose.presentation.auth

import com.example.jetpackcompose.data.remote.ApiService
import com.example.jetpackcompose.di.AuthApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.data.remote.dto.SendOtp
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.launch

@HiltViewModel
class SendOtpViewModel @Inject constructor(
    @AuthApi private val authApi: ApiService
) : ViewModel() {
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    var otpStatus: String = ""
        private set

    fun sendOtp(
        data: SendOtp,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = authApi.sendOtp(data)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        otpStatus = "OTP sent successfully: ${responseBody.message}"
                        onSuccess(otpStatus)
                    } else {
                        otpStatus = "Empty response from server"
                        onError(otpStatus)
                    }
                } else {
                    val errorBodyString = response.errorBody()?.string()
                    if (errorBodyString != null) {
                        try {
                            val errorResponse = gson.fromJson(errorBodyString, SendOtp::class.java)
                            val errorMessage = errorResponse.email
                            otpStatus = "OTP failed: $errorMessage"
                            onError(otpStatus)
                        } catch (e: JsonSyntaxException) {
                            otpStatus = "Error parsing error response: ${e.localizedMessage}"
                            onError(otpStatus)
                        } catch (e: JsonParseException) {
                            otpStatus = "Error parsing error response: ${e.localizedMessage}"
                            onError(otpStatus)
                        }
                    }
                }
            } catch (e: Exception) {
                otpStatus = "Error sending OTP: ${e.localizedMessage}"
                Log.e("SendOtpViewModel", "Error sending OTP: ${e.localizedMessage}", e)
                onError(otpStatus)
            }
        }
    }
}
