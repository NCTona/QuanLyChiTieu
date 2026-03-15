package com.example.jetpackcompose.presentation.auth

import com.example.jetpackcompose.data.remote.ApiService
import com.example.jetpackcompose.di.AuthApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.data.remote.dto.ResetPassword
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.launch

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    @AuthApi private val authApi: ApiService
) : ViewModel() {
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    var resetPasswordStatus: String = ""
        private set

    fun resetPassword(
        data: ResetPassword,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = authApi.resetPassword(data)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        resetPasswordStatus = "Password reset successfully: ${responseBody.message}"
                        onSuccess(resetPasswordStatus)
                    } else {
                        resetPasswordStatus = "Empty response from server"
                        onError(resetPasswordStatus)
                    }
                } else {
                    val errorBodyString = response.errorBody()?.string()
                    if (errorBodyString != null) {
                        try {
                            val errorResponse = gson.fromJson(errorBodyString, ResetPassword::class.java)
                            val errorMessage = errorResponse.newPassword
                            resetPasswordStatus = "Password reset failed: $errorMessage"
                            onError(resetPasswordStatus)
                        } catch (e: JsonSyntaxException) {
                            resetPasswordStatus = "Error parsing error response: ${e.localizedMessage}"
                            onError(resetPasswordStatus)
                        } catch (e: JsonParseException) {
                            resetPasswordStatus = "Error parsing error response: ${e.localizedMessage}"
                            onError(resetPasswordStatus)
                        }
                    } else {
                        resetPasswordStatus = "Empty error response from server"
                        onError(resetPasswordStatus)
                    }
                }
            } catch (e: Exception) {
                resetPasswordStatus = "Error: ${e.localizedMessage}"
                onError(resetPasswordStatus)
            }
        }
    }

}
