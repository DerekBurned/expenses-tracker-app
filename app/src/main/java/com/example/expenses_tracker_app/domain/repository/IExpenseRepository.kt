package com.example.expenses_tracker_app.domain.repository

import com.example.expenses_tracker_app.domain.model.Expense

interface IExpenseRepository {
    suspend fun performSync()
    suspend fun getAllExpenses(): List<Expense>


}