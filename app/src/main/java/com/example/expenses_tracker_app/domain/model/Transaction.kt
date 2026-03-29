package com.example.expenses_tracker_app.domain.model

import com.example.expenses_tracker_app.data.remote.TransactionDTO
import com.example.expenses_tracker_app.presentation.features.TransactionUiModel
import java.text.NumberFormat
import java.util.Locale

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

/**
 * FIX: was constructing the now-deleted sealed subtype TransactionDTO.ExpenseDTO /
 * TransactionDTO.IncomeDTO. TransactionDTO is now a flat data class.
 */
fun Transaction.toDTO(): TransactionDTO =
    when (this) {
        is Transaction.Expense -> TransactionDTO(
            localId         = localId,
            amount          = amount,
            description     = description,
            date            = date,
            transactionType = "EXPENSE",
            categoryLocalId = expenseType.name
        )
        is Transaction.Income -> TransactionDTO(
            localId         = localId,
            amount          = amount,
            description     = description,
            date            = date,
            transactionType = "INCOME",
            categoryLocalId = incomeType.name
        )
    }
 fun Transaction.toUiModel(): TransactionUiModel {
     val formatted = if (amount >= 0) "+${currencyFmt.format(amount)}"
     else currencyFmt.format(amount)
     return when (this) {
         is Transaction.Expense -> TransactionUiModel(
             id = localId,
             title = description,
             amount = amount,
             amountLabel = formatted,
             category = expenseType.name.replace("_", " "),
             isExpense = true
         )

         is Transaction.Income -> TransactionUiModel(
             id = localId,
             title = description,
             amount = amount,
             amountLabel = formatted,
             category = incomeType.name.replace("_", " "),
             isExpense = false
         )
     }
 }
private val currencyFmt = NumberFormat.getCurrencyInstance(Locale.US)
