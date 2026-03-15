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
import com.example.jetpackcompose.data.remote.dto.AnomalyResponse
import com.example.jetpackcompose.data.local.TokenStorage
import kotlinx.coroutines.launch

/**
 * ViewModel cho màn hình phát hiện giao dịch bất thường (Isolation Forest).
 */
@HiltViewModel
class AnomalyViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService
) : ViewModel() {

    private val tokenStorage = TokenStorage(context)

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var anomalies by mutableStateOf<List<AnomalyResponse>>(emptyList())
        private set

    /**
     * Lấy danh sách giao dịch bất thường từ server.
     */
    fun loadAnomalies() {
        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val token = tokenStorage.getAccessToken() ?: ""
                val response = apiService.getAnomalies("Bearer $token")
                
                if (response.isSuccessful && response.body() != null) {
                    anomalies = response.body()!!
                    Log.d("AnomalyViewModel", "Loaded ${anomalies.size} anomalies")
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                    errorMessage = "Server error: ${response.code()}"
                    Log.e("AnomalyViewModel", "Error body: $errorMsg")
                }
            } catch (e: com.google.gson.JsonSyntaxException) {
                errorMessage = "Lỗi dữ liệu: Server trả về sai định dạng."
                Log.e("AnomalyViewModel", "JSON Parse Error", e)
            } catch (e: Exception) {
                errorMessage = "Không thể kết nối server: ${e.message}"
                Log.e("AnomalyViewModel", "Exception", e)
            } finally {
                isLoading = false
            }
        }
    }
}
