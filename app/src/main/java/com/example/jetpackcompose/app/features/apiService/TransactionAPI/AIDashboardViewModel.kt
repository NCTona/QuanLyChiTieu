package com.example.jetpackcompose.app.features.apiService.TransactionAPI

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.app.features.apiService.AISummaryResponse
import com.example.jetpackcompose.app.features.apiService.RetrofitProvider
import kotlinx.coroutines.launch

/**
 * ViewModel cho AI Dashboard — gọi 1 API duy nhất lấy toàn bộ insights:
 * - Weekly Analysis: cung cấp dữ liệu cho TFLite (Android)
 * - LightGBM: dự đoán theo danh mục
 * - Isolation Forest: phát hiện bất thường
 * - Pattern: cảnh báo ngày chi cao
 */
class AIDashboardViewModel(context: Context) : ViewModel() {

    private val apiService = RetrofitProvider.provideApiService(context)

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var summary by mutableStateOf<AISummaryResponse?>(null)
        private set

    /**
     * Gọi GET /api/forecast/summary để lấy tất cả insights 1 lần.
     */
    fun loadSummary() {
        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val response = apiService.getAISummary()
                if (response.isSuccessful && response.body() != null) {
                    summary = response.body()
                    Log.d("AIDashboard", "Summary loaded successfully")
                } else {
                    errorMessage = "Server trả về lỗi: ${response.code()}"
                    Log.e("AIDashboard", "Error: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: com.google.gson.JsonSyntaxException) {
                errorMessage = "Lỗi định dạng dữ liệu từ server"
                Log.e("AIDashboard", "JSON parse error", e)
            } catch (e: Exception) {
                errorMessage = "Không thể kết nối server: ${e.message}"
                Log.e("AIDashboard", "Exception", e)
            } finally {
                isLoading = false
            }
        }
    }
}
