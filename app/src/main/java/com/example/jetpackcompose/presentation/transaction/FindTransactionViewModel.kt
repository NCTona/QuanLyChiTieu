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
import com.example.jetpackcompose.data.remote.dto.FindTransactionResponse
import kotlinx.coroutines.launch

@HiltViewModel
class FindTransactionViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService
) : ViewModel() {

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
                val response = apiService.findTransactions(note, categoryName, amount)
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
