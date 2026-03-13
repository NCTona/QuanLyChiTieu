package com.example.jetpackcompose.app.features.apiService.TransactionAPI

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.app.features.apiService.RetrofitProvider
import com.example.jetpackcompose.app.features.apiService.TransactionResponse
import com.example.jetpackcompose.app.screens.DailyTransaction
import kotlinx.coroutines.launch

class GetTransactionViewModel(private val context: Context) : ViewModel() {

    private val api = RetrofitProvider.provideApiService(context)

    var transactionList: List<DailyTransaction> = emptyList()
        private set

    var dateTransactionList: Map<String, List<TransactionResponse.TransactionDetail>> = emptyMap()

    var transactionStatus: String = ""
        private set

    fun getTransactions(
        month: Int,
        year: Int,
        onSuccess1: (List<DailyTransaction>) -> Unit,
        onSuccess2: (Map<String, List<TransactionResponse.TransactionDetail>>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = api.getTransactions(month, year)
                Log.d("TransactionViewModel", "Response Code: ${response.code()}")

                if (response.isSuccessful) {
                    val transactionsResponse = response.body()
                    if (transactionsResponse != null) {
                        val dailyTransactionList = transactionsResponse.dailyTransactions.map { (date, dailyTransaction) ->
                            DailyTransaction(
                                date = date,
                                amountIncome = dailyTransaction.totalDailyIncome,
                                amountExpense = dailyTransaction.totalDailyExpense
                            )
                        }

                        val dateTransactionDetailList: Map<String, List<TransactionResponse.TransactionDetail>> = transactionsResponse.transactions
                            .groupBy { transaction -> transaction.transactionDate.joinToString("-") }
                            .mapValues { (_, transactions) ->
                                transactions.map {
                                    TransactionResponse.TransactionDetail(
                                        categoryName = it.categoryName,
                                        amount = it.amount,
                                        transactionDate = it.transactionDate,
                                        note = it.note,
                                        type = it.type,
                                        transaction_id = it.transaction_id
                                    )
                                }
                            }

                        Log.d("TransactionViewModel", "TransactionDetails grouped by date: $dateTransactionDetailList")

                        transactionList = dailyTransactionList
                        dateTransactionList = dateTransactionDetailList
                        transactionStatus = "Transactions fetched successfully"
                        onSuccess1(transactionList)
                        onSuccess2(dateTransactionList)
                    } else {
                        transactionStatus = "Error: Empty response from server"
                        onError(transactionStatus)
                    }
                } else {
                    val errorBodyString = response.errorBody()?.string()
                    transactionStatus = "Error fetching transactions: $errorBodyString"
                    onError(transactionStatus)
                }
            } catch (e: Exception) {
                transactionStatus = "Error: ${e.localizedMessage}"
                Log.e("TransactionViewModel", "Error fetching transactions: ${e.localizedMessage}", e)
                onError(transactionStatus)
            }
        }
    }
}
