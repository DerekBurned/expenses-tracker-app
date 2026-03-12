package com.example.expenses_tracker_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.expenses_tracker_app.data.remote.TransactionDTO
import com.example.expenses_tracker_app.domain.model.ExpenseType
import com.example.expenses_tracker_app.domain.model.IncomeType
import com.example.expenses_tracker_app.domain.model.Transaction
import java.util.UUID

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val localId: String = UUID.randomUUID().toString(),
    val amount: Double = 0.0,
    val description: String = "",
    val category: String = "",
    val transactionType: String = "",
    val date: String = "",
    val isSynced: Boolean = false
)

// Entity → Domain
fun TransactionEntity.toDomain(): Transaction =
    when (transactionType) {
        "INCOME" -> Transaction.Income(
            localId = localId,
            amount = amount,
            description = description,
            date = date,
            incomeType = runCatching { IncomeType.valueOf(category) }.getOrDefault(IncomeType.OTHER)
        )
        else -> Transaction.Expense(
            localId = localId,
            amount = amount,
            description = description,
            date = date,
            expenseType = runCatching { ExpenseType.valueOf(category) }.getOrDefault(ExpenseType.DEFAULT)
        )
    }

// Domain → Entity
fun Transaction.toEntity(): TransactionEntity =
    when (val t = this) {
        is Transaction.Expense -> TransactionEntity(
            localId = t.localId,
            amount = t.amount,
            description = t.description,
            date = t.date,
            category = t.expenseType.name,
            transactionType = "EXPENSE"
        )
        is Transaction.Income -> TransactionEntity(
            localId = t.localId,
            amount = t.amount,
            description = t.description,
            date = t.date,
            category = t.incomeType.name,
            transactionType = "INCOME"
        )
    }

// Entity → DTO
fun TransactionEntity.toDTO(): TransactionDTO =
    when (transactionType) {
        "INCOME" -> TransactionDTO.IncomeDTO(
            localId = localId,
            amount = amount,
            description = description,
            date = date,
            categoryLocalId = category
        )
        else -> TransactionDTO.ExpenseDTO(
            localId = localId,
            amount = amount,
            description = description,
            date = date,
            categoryLocalId = category
        )
    }