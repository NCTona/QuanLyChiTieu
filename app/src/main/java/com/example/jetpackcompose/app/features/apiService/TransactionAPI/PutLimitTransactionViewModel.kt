package com.example.jetpackcompose.app.features.apiService.TransactionAPI

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.app.screens.LimitTransaction
import com.example.jetpackcompose.app.features.apiService.RetrofitProvider
import kotlinx.coroutines.launch

class PutLimitTransactionViewModel(private val context: Context) : ViewModel() {

    private val api = RetrofitProvider.provideApiService(context)

    var limitTransactionStatus: String = ""
        private set

    fun addLimitTransaction(
        data: List<LimitTransaction. CategoryLimit>,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("LimitTransactionViewModel", "Limit Transaction Data: $data")

                val response = api.addLimitTransaction(data)
                Log.d("LimitTransactionViewModel", "Response Code: ${response.code()}")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        limitTransactionStatus = "Limit transaction added successfully"
                        onSuccess(limitTransactionStatus)
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
                Log.e("LimitTransactionViewModel", "Error adding limit transaction: ${e.localizedMessage}", e)
                onError(limitTransactionStatus)
            }
        }
    }
}
