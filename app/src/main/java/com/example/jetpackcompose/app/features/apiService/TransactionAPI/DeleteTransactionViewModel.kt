package com.example.jetpackcompose.app.features.apiService.TransactionAPI

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.app.features.apiService.RetrofitProvider
import kotlinx.coroutines.launch

class DeleteTransactionViewModel(private val context: Context) : ViewModel() {

    private val api = RetrofitProvider.provideApiService(context)

    var transactionStatus: String = ""
        private set

    fun deleteTransaction(
        transactionId: Int,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("DeleteTransactionViewModel", "Transaction ID: $transactionId")

                val response = api.deleteTransaction(transactionId)
                Log.d("DeleteTransactionViewModel", "Response Code: ${response.code()}")

                if (response.isSuccessful) {
                    transactionStatus = "Giao dịch đã được xóa thành công"
                    onSuccess(transactionStatus)
                } else {
                    val errorBodyString = response.errorBody()?.string()
                    transactionStatus = "Lỗi xóa giao dịch: $errorBodyString"
                    onError(transactionStatus)
                }
            } catch (e: Exception) {
                transactionStatus = "Lỗi: ${e.localizedMessage}"
                Log.e("DeleteTransactionViewModel", "Error deleting transaction: ${e.localizedMessage}", e)
                onError(transactionStatus)
            }
        }
    }
}
