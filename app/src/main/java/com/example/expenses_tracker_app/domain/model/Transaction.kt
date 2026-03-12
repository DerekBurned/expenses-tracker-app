package com.example.expenses_tracker_app.domain.model

import com.example.expenses_tracker_app.data.remote.TransactionDTO

sealed class Transaction {
    abstract val localId: String
    abstract val amount: Double
    abstract val description: String
    abstract val date: String

    data class Expense(
        override val localId: String,
        override val amount: Double,
        override val description: String,
        override val date: String,
        val expenseType: ExpenseType
    ) : Transaction()

    data class Income(
        override val localId: String,
        override val amount: Double,
        override val description: String,
        override val date: String,
        val incomeType: IncomeType
    ) : Transaction()
}
fun Transaction.toDTO(): TransactionDTO =
    when (this) {
        is Transaction.Expense -> TransactionDTO.ExpenseDTO(
            localId = localId,
            amount = amount,
            description = description,
            date = date,
            categoryLocalId = expenseType.name
        )
        is Transaction.Income -> TransactionDTO.IncomeDTO(
            localId = localId,
            amount = amount,
            description = description,
            date = date,
            categoryLocalId = incomeType.name
        )
    }
