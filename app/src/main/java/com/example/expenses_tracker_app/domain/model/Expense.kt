package com.example.expenses_tracker_app.domain.model

data class Expense(
    val localId: String,
    val amount: Double,
    val description: String,
    val expenseDate: String
)