package com.example.jetpackcompose.data.remote.dto

data class FixedTransaction(
    val amount: Long,
    val category_id: Int,
    val title: String,
    val repeat_frequency: RepeatFrequency,
    val start_date: String,
    val end_date: String
)

enum class RepeatFrequency(val displayName: String) {
    daily("daily"),
    weekly("weekly"),
    monthly("monthly"),
    yearly("yearly");

    override fun toString(): String {
        return displayName
    }
}

data class GetFixedTransactionResponse(
    val fixedTransactionResponseList: List<FixedTransactionResponse>
)

data class FixedTransactionResponse(
    val title: String?,
    val categoryName: String,
    val amount: Long,
    val startDate: List<Int>,
    val endDate: List<Int>?,
    val fixed_transaction_id: Int,
    val category_id: Int,
    val repeate_frequency: String
)

data class FixedTransactionUpdate(
    val category_id: Int,
    val title: String,
    val amount: Long,
    val type: String,
    val repeat_frequency: String,
    val start_date: String,
    val end_date: String
)
