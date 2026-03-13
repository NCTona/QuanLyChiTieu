package com.example.jetpackcompose.app.features.apiService.TransactionAPI

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.app.features.apiService.FindTransactionResponse
import com.example.jetpackcompose.app.features.apiService.RetrofitProvider
import kotlinx.coroutines.launch

class FindTransactionViewModel(private val context: Context) : ViewModel() {

    private val api = RetrofitProvider.provideApiService(context)

    var searchResults: List<FindTransactionResponse> = emptyList()
        private set

    var searchStatus: String = ""
        private set

    fun findTransactions(
        amount: Long?,
        categoryName: String,
        note: String,
        onSuccess: (List<FindTransactionResponse>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = api.findTransactions(note, categoryName, amount)
                Log.d("FindTransactionViewModel", "Response Code: ${response.code()}")

                if (response.isSuccessful) {
                    val transactions = response.body()
                    if (transactions != null) {
                        searchResults = transactions
                        searchStatus = "Search completed successfully"
                        onSuccess(searchResults)
                    } else {
                        searchStatus = "No transactions found for the keyword: $note"
                        onError(searchStatus)
                    }
                } else {
                    val errorBodyString = response.errorBody()?.string()
                    searchStatus = "Error fetching transactions: $errorBodyString"
                    onError(searchStatus)
                }
            } catch (e: Exception) {
                searchStatus = "Error: ${e.localizedMessage}"
                Log.e("FindTransactionViewModel", "Error searching transactions: ${e.localizedMessage}", e)
                onError(searchStatus)
            }
        }
    }
}
