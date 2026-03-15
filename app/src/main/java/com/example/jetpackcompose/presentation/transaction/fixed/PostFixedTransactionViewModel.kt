package com.example.jetpackcompose.presentation.transaction.fixed

import com.example.jetpackcompose.data.remote.dto.Transaction
import com.example.jetpackcompose.data.remote.dto.FixedTransaction
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
class PostFixedTransactionViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService
) : ViewModel() {

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

                val response = apiService.addFixedTransaction(fixedExpense)
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
