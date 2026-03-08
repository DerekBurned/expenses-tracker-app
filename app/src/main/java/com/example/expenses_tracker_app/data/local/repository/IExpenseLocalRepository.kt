package com.example.expenses_tracker_app.data.local.repository

import com.example.expenses_tracker_app.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

interface IExpenseLocalRepository {
    fun getAllExpenses(): Flow<List<ExpenseEntity>>
    suspend fun addExpense(expense: ExpenseEntity)
    suspend fun markAsSynced(ids: List<String>)
    suspend fun deleteExpense(id: String)
    fun getUnsyncedExpenses(): List<ExpenseEntity>
}