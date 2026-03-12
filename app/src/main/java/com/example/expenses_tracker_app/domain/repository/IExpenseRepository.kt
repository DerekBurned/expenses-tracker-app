package com.example.expenses_tracker_app.domain.repository

import com.example.expenses_tracker_app.domain.model.Transaction

interface IExpenseRepository {
    suspend fun performSync()
    suspend fun getAllTransaction(): List<Transaction>
    suspend fun deleteTransaction(localId: String): Result<Boolean>

}