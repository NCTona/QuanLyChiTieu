package com.example.jetpackcompose.presentation.auth

import com.example.jetpackcompose.data.remote.ApiService
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.data.local.TokenStorage
import kotlinx.coroutines.launch

@HiltViewModel
class LogOutViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService
) : ViewModel() {

    var logoutStatus: String = ""
        private set

    fun logOutUser(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = apiService.logout()

                if (response.isSuccessful) {
                    // Xóa token sau khi logout thành công
                    TokenStorage(context).clear()
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
