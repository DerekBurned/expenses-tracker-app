package com.example.expenses_tracker_app.data.remote


data class ExpenseDto(
    val localId: String,
    val amount: Double,
    val description: String,
    val expenseDate: String // Sent as ISO 8601 string
)