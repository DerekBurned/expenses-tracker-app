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

// ─── Entity → Domain ──────────────────────────────────────────────────────────

fun TransactionEntity.toDomain(): Transaction =
    when (transactionType) {
        "INCOME" -> Transaction.Income(
            localId    = localId,
            amount     = amount,
            description = description,
            date       = date,
            incomeType = runCatching { IncomeType.valueOf(category) }.getOrDefault(IncomeType.OTHER)
        )
        else -> Transaction.Expense(
            localId     = localId,
            amount      = amount,
            description = description,
            date        = date,
            expenseType = runCatching { ExpenseType.valueOf(category) }.getOrDefault(ExpenseType.DEFAULT)
        )
    }

// ─── Domain → Entity ──────────────────────────────────────────────────────────

fun Transaction.toEntity(): TransactionEntity =
    when (this) {
        is Transaction.Expense -> TransactionEntity(
            localId         = localId,
            amount          = amount,
            description     = description,
            date            = date,
            category        = expenseType.name,
            transactionType = "EXPENSE"
        )
        is Transaction.Income -> TransactionEntity(
            localId         = localId,
            amount          = amount,
            description     = description,
            date            = date,
            category        = incomeType.name,
            transactionType = "INCOME"
        )
    }

// ─── Entity → DTO (for sync worker) ──────────────────────────────────────────

/**
 * FIX: was constructing TransactionDTO.ExpenseDTO / TransactionDTO.IncomeDTO
 * (sealed subtypes). TransactionDTO is now a flat data class — construct it
 * directly with the transactionType discriminator set correctly.
 */
fun TransactionEntity.toDTO(): TransactionDTO =
    TransactionDTO(
        localId         = localId,
        amount          = amount,
        description     = description,
        date            = date,
        transactionType = transactionType,   // "EXPENSE" or "INCOME"
        categoryLocalId = category
    )