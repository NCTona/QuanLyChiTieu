package com.example.jetpackcompose.app.features.apiService.FixedTransactionAPI

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.app.features.apiService.ApiService
import com.example.jetpackcompose.app.features.apiService.RetrofitProvider
import kotlinx.coroutines.launch

class GetFixedTransactionViewModel(private val context: Context) : ViewModel() {
    private val api = RetrofitProvider.provideApiService(context)

    var transactionStatus: String = ""
        private set

    fun getFixedTransactions(
        onSuccess: (List<FixedTransactionResponse>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = api.getFixedTransactions()
                Log.d("GetFixedTransactionViewModel", "Response Code: ${response.code()}")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        transactionStatus = "Fixed Expenses fetched successfully"
                        onSuccess(responseBody.fixedTransactionResponseList)
                    } else {
                        transactionStatus = "Error: Empty response from server"
                        onError(transactionStatus)
                    }
                } else {
                    val errorBodyString = response.errorBody()?.string()
                    transactionStatus = "Error fetching Fixed Expenses: $errorBodyString"
                    onError(transactionStatus)
                }
            } catch (e: Exception) {
                transactionStatus = "Error: ${e.localizedMessage}"
                Log.e("GetFixedTransactionViewModel", "Error fetching Fixed Expenses: ${e.localizedMessage}", e)
                onError(transactionStatus)
            }
        }
    }
}
