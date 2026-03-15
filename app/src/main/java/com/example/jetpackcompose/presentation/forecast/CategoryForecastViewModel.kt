package com.example.jetpackcompose.presentation.forecast

import com.example.jetpackcompose.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.data.remote.dto.CategoryForecastResponse
import com.example.jetpackcompose.data.remote.dto.TrendResponse
import kotlinx.coroutines.launch

/**
 * ViewModel cho man hinh du doan chi tieu theo danh muc (LightGBM server-side).
 */
@HiltViewModel
class CategoryForecastViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var forecasts by mutableStateOf<List<CategoryForecastResponse>>(emptyList())
        private set

    var selectedTrend by mutableStateOf<TrendResponse?>(null)
        private set

    var isTrendLoading by mutableStateOf(false)
        private set

    /**
     * Lay du doan chi tieu tat ca danh muc tu server.
     * @param normalized true = ràng buộc budget, false = dự đoán tự do (mặc định).
     */
    fun loadForecasts(normalized: Boolean = false) {
        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val response = apiService.getCategoryForecasts(normalized)
                if (response.isSuccessful && response.body() != null) {
                    forecasts = response.body()!!.filter { it.category_id != 9L }
                    Log.d("CategoryForecast", "Loaded ${forecasts.size} category forecasts (normalized=$normalized)")
                } else {
                    errorMessage = "Server tra ve loi: ${response.code()}"
                    Log.e("CategoryForecast", "Error: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                errorMessage = "Khong the ket noi server: ${e.message}"
                Log.e("CategoryForecast", "Exception", e)
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Phan tich xu huong chi tieu so voi so dong cho 1 danh muc.
     */
    fun loadTrend(categoryId: Long) {
        isTrendLoading = true
        selectedTrend = null

        viewModelScope.launch {
            try {
                val response = apiService.getCategoryTrend(categoryId)
                if (response.isSuccessful && response.body() != null) {
                    selectedTrend = response.body()
                    Log.d("CategoryForecast", "Trend loaded for category $categoryId")
                } else {
                    Log.e("CategoryForecast", "Trend error: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("CategoryForecast", "Trend exception", e)
            } finally {
                isTrendLoading = false
            }
        }
    }
}
