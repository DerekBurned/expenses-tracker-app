package com.example.expenses_tracker_app.data.remote

import com.example.expenses_tracker_app.data.local.entity.TransactionEntity
import com.example.expenses_tracker_app.domain.model.ExpenseType
import com.example.expenses_tracker_app.domain.model.IncomeType
import com.example.expenses_tracker_app.domain.model.Transaction
import com.google.gson.annotations.SerializedName

data class TransactionDTO(
    @SerializedName("localId")
    val localId: String = "",

    @SerializedName("amount")
    val amount: Double = 0.0,

    @SerializedName("description")
    val description: String = "",

    @SerializedName("date")
    val date: String = "",

    @SerializedName("transactionType")
    val transactionType: String = "EXPENSE",

    @SerializedName("categoryLocalId")
    val categoryLocalId: String = ""
)

// ─── DTO → Domain ─────────────────────────────────────────────────────────────

fun TransactionDTO.toDomain(): Transaction =
    when (transactionType) {
        "INCOME" -> Transaction.Income(
            localId     = localId,
            amount      = amount,
            description = description,
            date        = date,
            incomeType  = runCatching { IncomeType.valueOf(categoryLocalId) }
                .getOrDefault(IncomeType.OTHER)
        )
        else -> Transaction.Expense(
            localId     = localId,
            amount      = amount,
            description = description,
            date        = date,
            expenseType = runCatching { ExpenseType.valueOf(categoryLocalId) }
                .getOrDefault(ExpenseType.DEFAULT)
        )
    }

// ─── DTO → Entity (for upsert after remote fetch) ────────────────────────────

fun TransactionDTO.toEntity(): TransactionEntity =
    TransactionEntity(
        localId         = localId,
        amount          = amount,
        description     = description,
        date            = date,
        category        = categoryLocalId,
        transactionType = transactionType,
        isSynced        = true   // came from server, already synced
    )