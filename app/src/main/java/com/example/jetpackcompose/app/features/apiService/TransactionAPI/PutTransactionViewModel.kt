package com.example.jetpackcompose.app.features.apiService.TransactionAPI

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.app.screens.Transaction
import com.example.jetpackcompose.app.features.apiService.RetrofitProvider
import kotlinx.coroutines.launch

class PutTransactionViewModel(private val context: Context) : ViewModel() {

    private val api = RetrofitProvider.provideApiService(context)

    var transactionStatus: String = ""
        private set

    fun putTransaction(
        transactionId: Int,
        data: Transaction,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("PutTransactionViewModel", "Transaction Data: $data")

                val response = api.putTransaction(transactionId, data)
                Log.d("PutTransactionViewModel", "Response Code: ${response.code()}")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        transactionStatus = "Giao dịch đã được cập nhật thành công"
                        onSuccess(transactionStatus)
                    } else {
                        transactionStatus = "Lỗi: Phản hồi từ server trống"
                        onError(transactionStatus)
                    }
                } else {
                    val errorBodyString = response.errorBody()?.string()
                    transactionStatus = "Lỗi cập nhật giao dịch: $errorBodyString"
                    onError(transactionStatus)
                }
            } catch (e: Exception) {
                transactionStatus = "Lỗi: ${e.localizedMessage}"
                Log.e("PutTransactionViewModel", "Error updating transaction: ${e.localizedMessage}", e)
                onError(transactionStatus)
            }
        }
    }
}
