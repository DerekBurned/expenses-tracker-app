package com.example.expenses_tracker_app.data.remote

import com.example.expenses_tracker_app.data.local.entity.ExpenseEntity
import com.example.expenses_tracker_app.domain.model.Category
import com.example.expenses_tracker_app.domain.model.Expense
import com.google.gson.annotations.SerializedName

data class ExpenseDTO(
    @SerializedName("localId")     val localId: String,
    @SerializedName("amount")      val amount: Double,
    @SerializedName("description") val description: String,
    @SerializedName("category")    val category: String,
    @SerializedName("date")        val expenseDate: String
)

fun ExpenseDTO.toDomain() = Expense(
    localId = localId,
    amount = amount,
    description = description,
    category = Category.valueOf(category),
    expenseDate = expenseDate
)

fun ExpenseDTO.toEntity(): ExpenseEntity {
    val dto = this
    return ExpenseEntity().apply {
        localId = dto.localId
        amount = dto.amount
        description = dto.description
        category = dto.category
        expenseDate = dto.expenseDate
        isSynced = true
    }
}