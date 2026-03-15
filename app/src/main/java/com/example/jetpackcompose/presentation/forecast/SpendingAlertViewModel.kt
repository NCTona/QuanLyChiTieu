package com.example.jetpackcompose.presentation.forecast

import com.example.jetpackcompose.data.remote.ApiService
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.data.remote.dto.SpendingAlertResponse
import com.example.jetpackcompose.data.local.TokenStorage
import kotlinx.coroutines.launch

/**
 * ViewModel cho màn hình cảnh báo chu kỳ chi tiêu (Pattern Alert).
 */
@HiltViewModel
class SpendingAlertViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService
) : ViewModel() {

    private val tokenStorage = TokenStorage(context)

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var alerts by mutableStateOf<List<SpendingAlertResponse>>(emptyList())
        private set

    /**
     * Lấy danh sách cảnh báo chi tiêu từ server.
     */
    fun loadAlerts() {
        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val token = tokenStorage.getAccessToken() ?: ""
                val response = apiService.getSpendingAlerts("Bearer $token")
                
                if (response.isSuccessful && response.body() != null) {
                    alerts = response.body()!!
                    Log.d("SpendingAlertViewModel", "Loaded ${alerts.size} alerts")
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                    errorMessage = "Server error: ${response.code()}"
                    Log.e("SpendingAlertViewModel", "Error body: $errorMsg")
                }
            } catch (e: com.google.gson.JsonSyntaxException) {
                errorMessage = "Lỗi dữ liệu: Server trả về sai định dạng."
                Log.e("SpendingAlertViewModel", "JSON Parse Error", e)
            } catch (e: Exception) {
                errorMessage = "Không thể kết nối server: ${e.message}"
                Log.e("SpendingAlertViewModel", "Exception", e)
            } finally {
                isLoading = false
            }
        }
    }
}
