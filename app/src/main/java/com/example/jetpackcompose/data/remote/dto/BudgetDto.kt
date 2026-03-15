package com.example.jetpackcompose.data.remote.dto

data class LimitTransaction(
    val limits: List<CategoryLimit>
) {
    data class CategoryLimit(
        val categoryId: Int,
        val limitExpense: Long
    )
}

data class RemainLimit(
    val limits: List<CategoryLimit>
) {
    data class CategoryLimit(
        val categoryId: Int,
        val limitExpense: Long,
        val remainingPercent: Double
    )
}
