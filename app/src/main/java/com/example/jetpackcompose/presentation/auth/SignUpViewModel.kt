package com.example.jetpackcompose.presentation.auth

import com.example.jetpackcompose.data.remote.ApiService
import com.example.jetpackcompose.di.AuthApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.data.remote.dto.RegistrationData
import com.example.jetpackcompose.data.remote.dto.RegistrationResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.launch

@HiltViewModel
class SignUpViewModel @Inject constructor(
    @AuthApi private val authApi: ApiService
) : ViewModel() {
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    var registrationStatus: String = ""
        private set

    fun registerUser(
        data: RegistrationData,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = authApi.register(data)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        registrationStatus = "Registration successful: ${responseBody.message}"
                        onSuccess(registrationStatus)
                    } else {
                        registrationStatus = "Empty response from server"
                        onError(registrationStatus)
                    }
                } else {
                    val errorBodyString = response.errorBody()?.string()
                    if (errorBodyString != null) {
                        try {
                            val errorResponse = gson.fromJson(errorBodyString, RegistrationResponse::class.java)
                            val errorMessage = errorResponse.message
                            registrationStatus = "Registration failed: $errorMessage"
                            onError(registrationStatus)
                        } catch (e: JsonSyntaxException) {
                            registrationStatus = "Error parsing error response: ${e.localizedMessage}"
                            onError(registrationStatus)
                        } catch (e: JsonParseException) {
                            registrationStatus = "Error parsing error response: ${e.localizedMessage}"
                            onError(registrationStatus)
                        }
                    } else {
                        registrationStatus = "Registration failed: Unknown error"
                        onError(registrationStatus)
                    }
                }
            } catch (e: Exception) {
                registrationStatus = "Error: ${e.localizedMessage}"
                Log.e("SignUpViewModel", "Error: ${e.localizedMessage}", e)
                onError(registrationStatus)
            }
        }
    }
}
