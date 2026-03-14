package com.example.jetpackcompose.app.features.apiService.TransactionAPI

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.app.features.apiService.ExpenseForecastHelper
import kotlinx.coroutines.launch

/**
 * ViewModel quản lý dự đoán chi tiêu TUẦN TỚI bằng model TFLite.
 * Không cần userId — model universal cho mọi user.
 */
class ForecastViewModel(private val context: Context) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var statusMessage by mutableStateOf("Chưa tải model")
        private set

    var isModelReady by mutableStateOf(false)
        private set

    var predictedAmount by mutableStateOf<Double?>(null)
        private set

    init {
        // Kiểm tra model đã có sẵn chưa
        if (ExpenseForecastHelper.isModelAvailable(context)) {
            val loaded = ExpenseForecastHelper.loadModel(context)
            isModelReady = loaded
            statusMessage = if (loaded) "Model đã sẵn sàng" else "Lỗi load model"
        }
    }

    /**
     * Tải model mới nhất từ server.
     */
    fun downloadModel() {
        isLoading = true
        statusMessage = "Đang tải model từ server..."

        viewModelScope.launch {
            val success = ExpenseForecastHelper.downloadAndSaveModel(context)
            if (success) {
                val loaded = ExpenseForecastHelper.loadModel(context)
                isModelReady = loaded
                statusMessage = if (loaded) "Model tai va load thanh cong!" else "Tai xong nhung loi load model"
            } else {
                statusMessage = "Tai model that bai. Kiem tra ket noi server."
            }
            isLoading = false
        }
    }

    /**
     * Chạy dự đoán chi tiêu TUẦN tiếp theo.
     * Aggregate dữ liệu theo tuần từ API giao dịch tháng hiện tại.
     */
    fun runPrediction() {
        if (!isModelReady) {
            statusMessage = "Can tai model truoc!"
            return
        }

        statusMessage = "Đang đồng bộ dữ liệu input 4 tuần chuẩn từ Server..."
        isLoading = true

        val apiService = com.example.jetpackcompose.app.features.apiService.RetrofitProvider.provideApiService(context)

        viewModelScope.launch {
            try {
                val response = apiService.getAISummary()
                if (response.isSuccessful && response.body() != null) {
                    val summary = response.body()
                    val inputWeeks = summary?.weekly_forecast?.input_weeks
                    
                    if (inputWeeks != null && inputWeeks.size == 4) {
                        Log.d("ForecastViewModel", "Fetched input_weeks from Server: $inputWeeks")
                        processFinalDataFromDetails(inputWeeks)
                    } else {
                        statusMessage = "Dữ liệu tuần không hợp lệ từ Server"
                        isLoading = false
                    }
                } else {
                    statusMessage = "Lỗi lấy data từ Server: ${response.code()}"
                    isLoading = false
                }
            } catch (e: Exception) {
                statusMessage = "Không thể kết nối Server: ${e.message}"
                isLoading = false
                Log.e("ForecastViewModel", "Error fetching AI summary", e)
            }
        }
    }

    private fun processFinalDataFromDetails(weeklyExpenses: List<Double>) {
        val final4Weeks = weeklyExpenses
        Log.d("ForecastViewModel", "final4Weeks (Calendar based): $final4Weeks")

        // Tính scale value (Max của 4 tuần)
        val maxVal = final4Weeks.maxOrNull() ?: 0.0
        val scaleValue = if (maxVal > 0) maxVal else 2000000.0

        statusMessage = "Đang dự đoán dựa trên data 4 tuần..."
        val result = ExpenseForecastHelper.predict(final4Weeks, scaleValue)

        if (result != null) {
            predictedAmount = result
            statusMessage = "Du doan hoan tat!"
        } else {
            statusMessage = "Loi khi chay du doan"
        }
        isLoading = false
    }

    override fun onCleared() {
        super.onCleared()
        ExpenseForecastHelper.close()
    }
}
