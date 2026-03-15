package com.example.jetpackcompose.data.remote.dto

data class Transaction(
    val category_id: Int,
    val amount: Long,
    val transaction_date: String,
    val note: String
)
