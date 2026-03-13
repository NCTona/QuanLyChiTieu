package com.example.jetpackcompose.app.features.apiService.FixedTransactionAPI

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.app.features.apiService.ApiService
import com.example.jetpackcompose.app.features.apiService.RetrofitProvider
import kotlinx.coroutines.launch

class PutFixedTransactionViewModel(private val context: Context) : ViewModel() {
    private val api = RetrofitProvider.provideApiService(context)

    var fixedTransactionStatus: String = ""
        private set

    fun putFixedTransaction(
        fixed_transaction_id: Int,
        data: FixedTransactionUpdate,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("PutFixedTransactionViewModel", "Fixed Transaction ID: $fixed_transaction_id")
                Log.d("PutFixedTransactionViewModel", "Fixed Transaction Data: $data")

                val response = api.putFixedTransaction(fixed_transaction_id, data)
                Log.d("PutFixedTransactionViewModel", "Response Code: ${response.code()}")

                if (response.isSuccessful) {
                    fixedTransactionStatus = "Giao dịch cố định đã được cập nhật thành công"
                    onSuccess(fixedTransactionStatus)
                } else {
                    val errorBodyString = response.errorBody()?.string()
                    fixedTransactionStatus = "Lỗi cập nhật giao dịch cố định: $errorBodyString"
                    onError(fixedTransactionStatus)
                }
            } catch (e: Exception) {
                fixedTransactionStatus = "Lỗi: ${e.message}"
                onError(fixedTransactionStatus)
            }
        }
    }
}
