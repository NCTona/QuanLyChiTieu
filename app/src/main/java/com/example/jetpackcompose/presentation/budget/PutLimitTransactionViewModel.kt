package com.example.jetpackcompose.presentation.budget

import com.example.jetpackcompose.data.remote.dto.Transaction
import com.example.jetpackcompose.data.remote.ApiService
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.data.remote.dto.LimitTransaction
import kotlinx.coroutines.launch

@HiltViewModel
class PutLimitTransactionViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService
) : ViewModel() {

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

                val response = apiService.addLimitTransaction(data)
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
