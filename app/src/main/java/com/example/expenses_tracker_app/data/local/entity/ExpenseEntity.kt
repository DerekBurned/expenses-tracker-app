package com.example.expenses_tracker_app.data.local.entity

import com.example.expenses_tracker_app.data.remote.TransactionDTO
import com.example.expenses_tracker_app.domain.model.ExpenseType
import com.example.expenses_tracker_app.domain.model.Transaction
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class ExpenseEntity : RealmObject {
    @PrimaryKey
    var localId: String = ObjectId().toString()
    var amount: Double = 0.0
    var description: String = ""
    var category: String = ""
    var expenseDate: String = ""
    var isSynced: Boolean = false
}

/*fun ExpenseEntity.toDomain() = Transaction(
    localId = localId,
    amount = amount,
    description = description,
    expenseType = ExpenseType.valueOf(category),
    expenseDate = expenseDate
)*/

/*fun Transaction.toEntity(): ExpenseEntity {
    val expense = this
    return ExpenseEntity().apply {
        localId = expense.localId
        amount = expense.amount
        description = expense.description
        category = expense.expenseType.name
        expenseDate = expense.expenseDate
    }
}
fun ExpenseEntity.toDTO() = TransactionDTO(
    localId = localId,
    amount = amount,
    description = description,
    category = category,
    expenseDate = expenseDate
)*/
