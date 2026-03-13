package com.example.jetpackcompose.app.features.apiService.LogAPI

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.app.features.apiService.RetrofitProvider
import com.example.jetpackcompose.app.features.apiService.TokenStorage
import kotlinx.coroutines.launch

class LogOutViewModel(private val context: Context) : ViewModel() {

    private val api = RetrofitProvider.provideApiService(context)

    var logoutStatus: String = ""
        private set

    fun logOutUser(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = api.logout()

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
