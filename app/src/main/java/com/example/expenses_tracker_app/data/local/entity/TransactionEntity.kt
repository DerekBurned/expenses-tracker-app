package com.example.expenses_tracker_app.data.local.entity

import com.example.expenses_tracker_app.data.remote.TransactionDTO
import com.example.expenses_tracker_app.domain.model.ExpenseType
import com.example.expenses_tracker_app.domain.model.IncomeType
import com.example.expenses_tracker_app.domain.model.Transaction
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class TransactionEntity : RealmObject {
    @PrimaryKey
    var localId: String = ObjectId().toString()
    var amount: Double = 0.0
    var description: String = ""
    var category: String = ""
    var transactionType: String = ""
    var date: String = ""
    var isSynced: Boolean = false
}

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
fun Transaction.toEntity(): TransactionEntity = TransactionEntity().apply {
    when (val t = this@toEntity) {
        is Transaction.Expense -> {
            localId = t.localId
            amount = t.amount
            description = t.description
            date = t.date
            category = t.expenseType.name
            transactionType = "EXPENSE"
        }
        is Transaction.Income -> {
            localId = t.localId
            amount = t.amount
            description = t.description
            date = t.date
            category = t.incomeType.name
            transactionType = "INCOME"
        }
    }
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
