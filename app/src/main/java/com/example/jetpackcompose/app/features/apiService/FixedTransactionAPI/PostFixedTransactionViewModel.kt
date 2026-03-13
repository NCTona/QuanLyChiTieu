package com.example.jetpackcompose.app.features.apiService.FixedTransactionAPI

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.app.features.apiService.ApiService
import com.example.jetpackcompose.app.features.apiService.RetrofitProvider
import kotlinx.coroutines.launch

class PostFixedTransactionViewModel(private val context: Context) : ViewModel() {
    private val api = RetrofitProvider.provideApiService(context)

    var transactionStatus: String = ""
        private set

    fun addFixedTransaction (
        fixedExpense: FixedTransaction,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("FixedExpenseViewModel", "FixedExpense Data: $fixedExpense")

                val response = api.addFixedTransaction(fixedExpense)
                Log.d("FixedExpenseViewModel", "Response Code: ${response.code()}")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        transactionStatus = "Fixed Expense added successfully"
                        onSuccess(transactionStatus)
                    } else {
                        transactionStatus = "Error: Empty response from server"
                        onError(transactionStatus)
                    }
                } else {
                    val errorBodyString = response.errorBody()?.string()
                    transactionStatus = "Error adding Fixed Expense: $errorBodyString"
                    onError(transactionStatus)
                }
            } catch (e: Exception) {
                transactionStatus = "Error: ${e.localizedMessage}"
                Log.e("FixedExpenseViewModel", "Error adding Fixed Expense: ${e.localizedMessage}", e)
                onError(transactionStatus)
            }
        }
    }
}
