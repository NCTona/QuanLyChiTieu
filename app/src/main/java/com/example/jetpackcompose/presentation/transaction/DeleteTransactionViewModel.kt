package com.example.jetpackcompose.presentation.transaction

import com.example.jetpackcompose.data.remote.dto.Transaction
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
class DeleteTransactionViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService
) : ViewModel() {

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

                val response = apiService.deleteTransaction(transactionId)
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
