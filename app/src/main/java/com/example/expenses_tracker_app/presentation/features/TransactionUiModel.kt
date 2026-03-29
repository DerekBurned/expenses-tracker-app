package com.example.expenses_tracker_app.presentation.features

data class TransactionUiModel(
    val id: String,
    val title: String,
    val amount: Double,
    val amountLabel: String,   // pre-formatted, e.g. "+$2,000.00"
    val category: String,
    val isExpense: Boolean
)