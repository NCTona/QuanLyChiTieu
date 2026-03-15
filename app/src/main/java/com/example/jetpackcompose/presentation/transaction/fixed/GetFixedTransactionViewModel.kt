package com.example.jetpackcompose.presentation.transaction.fixed

import com.example.jetpackcompose.data.remote.dto.Transaction
import com.example.jetpackcompose.data.remote.dto.FixedTransactionResponse
import com.example.jetpackcompose.data.remote.ApiService
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@HiltViewModel
class GetFixedTransactionViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService
) : ViewModel() {

    var transactionStatus: String = ""
        private set

    fun getFixedTransactions(
        onSuccess: (List<FixedTransactionResponse>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = apiService.getFixedTransactions()
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
