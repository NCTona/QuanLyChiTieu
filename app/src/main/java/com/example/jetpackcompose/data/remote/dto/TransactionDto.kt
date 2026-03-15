package com.example.jetpackcompose.data.remote.dto

data class TransactionResponse(
    val dailyTransactions: Map<String, DailyTransaction>,
    val totalIncome: Long,
    val totalExpense: Long,
    val balance: Long,
    val transactions: List<TransactionDetail>
) {

    data class DailyTransaction(
        val totalDailyIncome: Long,
        val totalDailyExpense: Long
    )

    data class TransactionDetail(
        val categoryName: String,
        val amount: Long,
        val transactionDate: List<Int>,
        val note: String?,
        val type: String?,
        val transaction_id: Int
    )
}

data class PostTransactionResponse(
    val categoryName: String,
    val amount: Double,
    val transactionDate: List<Int>,
    val note: String,
    val type: String,
    val transaction_id: Long
)

data class FindTransactionResponse(
    val categoryName: String,
    val amount: Long,
    val transactionDate: List<Int>,
    val note: String?,
    val type: String,
    val transaction_id: Long
)

data class ReportExpenseResponse(
    val totalIncome: Long,
    val totalExpense: Long,
    val netAmount: Long,
    val categoryExpenseReports: List<CategoryExpenseReport>
){
    data class CategoryExpenseReport(
        val categoryId: Int,
        val categoryName: String,
        val spentAmount: Long,
        val percentSpent: Double,
        val percentLimit: Double
    )
}

data class ReportIncomeResponse(
    val totalIncome: Long,
    val totalExpense: Long,
    val netAmount: Long,
    val categoryIncomeReports: List<CategoryIncomeReport>
){
    data class CategoryIncomeReport(
        val categoryId: Int,
        val categoryName: String,
        val categoryIncome: Long,
        val percentIncome: Double
    )
}
