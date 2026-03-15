package com.example.jetpackcompose.presentation.transaction.fixed

import com.example.jetpackcompose.data.remote.dto.Transaction
import com.example.jetpackcompose.data.remote.dto.FixedTransactionUpdate
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
class PutFixedTransactionViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService
) : ViewModel() {

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

                val response = apiService.putFixedTransaction(fixed_transaction_id, data)
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
