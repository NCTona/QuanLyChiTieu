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

        statusMessage = "Đang lấy dữ liệu chi tiêu 2 tháng gần nhất..."
        isLoading = true

        val calendar = java.util.Calendar.getInstance()
        val currentMonth = calendar.get(java.util.Calendar.MONTH) + 1
        val currentYear = calendar.get(java.util.Calendar.YEAR)

        // Tính tháng trước
        calendar.add(java.util.Calendar.MONTH, -1)
        val prevMonth = calendar.get(java.util.Calendar.MONTH) + 1
        val prevYear = calendar.get(java.util.Calendar.YEAR)

        val getTransViewModel = GetTransactionViewModel(context)
        val allDailyTransactions = mutableListOf<com.example.jetpackcompose.app.screens.DailyTransaction>()

        // Lấy tháng hiện tại
        getTransViewModel.getTransactions(
            month = currentMonth,
            year = currentYear,
            onSuccess1 = { currentMonthList ->
                allDailyTransactions.addAll(currentMonthList)
                
                // Sau đó lấy thêm tháng trước
                getTransViewModel.getTransactions(
                    month = prevMonth,
                    year = prevYear,
                    onSuccess1 = { prevMonthList ->
                        allDailyTransactions.addAll(prevMonthList)
                        processFinalData(allDailyTransactions)
                    },
                    onSuccess2 = {},
                    onError = { processFinalData(allDailyTransactions) } // Vẫn xử lý nếu tháng trước lỗi
                )
            },
            onSuccess2 = { _ -> },
            onError = { error ->
                statusMessage = "Loi lay data: $error"
                isLoading = false
            }
        )
    }

    private fun processFinalData(dailyList: List<com.example.jetpackcompose.app.screens.DailyTransaction>) {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        
        // Tạo map tra cứu nhanh: "yyyy-M-d" -> amount
        val dailyMap = dailyList.associate { it.date to it.amountExpense.toDouble() }

        // Mảng chứa 28 ngày gần nhất (từ cũ đến mới)
        val last28DaysAmounts = DoubleArray(28)
        val calendar = java.util.Calendar.getInstance()
        
        // Lùi 27 ngày để bắt đầu từ ngày cũ nhất trong chuỗi 28 ngày
        calendar.add(java.util.Calendar.DAY_OF_YEAR, -27)

        for (i in 0 until 28) {
            val dateStr = sdf.format(calendar.time)
            last28DaysAmounts[i] = dailyMap[dateStr] ?: 0.0
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1) // Tiến 1 ngày
        }

        // Chia 28 ngày thành 4 tuần (mỗi tuần 7 ngày)
        val weeklyExpenses = mutableListOf<Double>()
        for (w in 0 until 4) {
            var weekSum = 0.0
            for (d in 0 until 7) {
                weekSum += last28DaysAmounts[w * 7 + d]
            }
            weeklyExpenses.add(weekSum)
        }

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
