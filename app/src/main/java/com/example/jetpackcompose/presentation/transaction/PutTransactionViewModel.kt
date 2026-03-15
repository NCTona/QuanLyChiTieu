package com.example.jetpackcompose.presentation.transaction

import com.example.jetpackcompose.data.remote.ApiService
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.data.remote.dto.Transaction
import kotlinx.coroutines.launch

@HiltViewModel
class PutTransactionViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService
) : ViewModel() {

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

                val response = apiService.putTransaction(transactionId, data)
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
