package com.example.expenses_tracker_app.data.remote

import com.example.expenses_tracker_app.domain.model.ExpenseType
import com.example.expenses_tracker_app.domain.model.IncomeType
import com.example.expenses_tracker_app.domain.model.Transaction
import com.google.gson.annotations.SerializedName

sealed class TransactionDTO{
   abstract val localId: String
   abstract val amount: Double
   abstract val description: String
   abstract val date: String


    data class ExpenseDTO(
        override val localId: String,
        override val amount: Double,
        override val description: String,
        override val date: String,
        @SerializedName("transactionType") val transactionType: String = "EXPENSE",
        @SerializedName("category") val categoryLocalId: String = "FOOD"  // ← add this
    ) : TransactionDTO()

    data class IncomeDTO(
        override val localId: String,
        override val amount: Double,
        override val description: String,
        override val date: String,
        @SerializedName("transactionType") val transactionType: String = "INCOME",  // ← fix this
        @SerializedName("categoryLocalId") val categoryLocalId: String = "OTHER" // ← add this
    ) : TransactionDTO()
}
fun TransactionDTO.toDomain(): Transaction =
    when (this) {
        is TransactionDTO.IncomeDTO -> Transaction.Income(
            localId = localId,
            amount = amount,
            description = description,
            date = date,
            incomeType = runCatching { IncomeType.valueOf(categoryLocalId) }.getOrDefault(IncomeType.OTHER)
        )
        is TransactionDTO.ExpenseDTO -> Transaction.Expense(
            localId = localId,
            amount = amount,
            description = description,
            date = date,
            expenseType = runCatching { ExpenseType.valueOf(categoryLocalId) }.getOrDefault(ExpenseType.DEFAULT)
        )
    }



