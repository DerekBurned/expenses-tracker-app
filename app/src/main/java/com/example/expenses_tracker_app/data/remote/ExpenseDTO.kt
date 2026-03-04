package com.example.expenses_tracker_app.data.remote

data class ExpenseDto(
    val localId: String,
    val amount: Double,
    val description: String,
    val expenseDate: String // Changed to String to match the ISO formatted date being passed
)