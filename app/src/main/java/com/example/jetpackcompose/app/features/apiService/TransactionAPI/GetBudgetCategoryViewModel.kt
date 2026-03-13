package com.example.jetpackcompose.app.features.apiService.TransactionAPI

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.app.screens.RemainLimit
import com.example.jetpackcompose.app.features.apiService.RetrofitProvider
import kotlinx.coroutines.launch

class GetBudgetCategoryViewModel(private val context: Context) : ViewModel() {

    private val api = RetrofitProvider.provideApiService(context)

    var limitTransactionStatus: String = ""
        private set

    fun getBudgetTransaction(
        onSuccess: (List<RemainLimit.CategoryLimit>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = api.getBudgetCategory()
                Log.d("LimitTransactionViewModel", "Response Code: ${response.code()}")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        onSuccess(responseBody)
                    } else {
                        limitTransactionStatus = "Error: Empty response from server"
                        onError(limitTransactionStatus)
                    }
                } else {
                    val errorBodyString = response.errorBody()?.string()
                    limitTransactionStatus = "Error adding limit transaction: $errorBodyString"
                    onError(limitTransactionStatus)
                }
            } catch (e: Exception) {
                limitTransactionStatus = "Error: ${e.localizedMessage}"
                Log.e("LimitTransactionViewModel", "Error adding budge category: ${e.localizedMessage}", e)
                onError(limitTransactionStatus)
            }
        }
    }
}
