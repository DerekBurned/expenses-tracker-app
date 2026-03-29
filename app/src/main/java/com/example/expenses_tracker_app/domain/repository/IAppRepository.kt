package com.example.expenses_tracker_app.domain.repository

import com.example.expenses_tracker_app.domain.model.Settings
import com.example.expenses_tracker_app.domain.model.Transaction

interface IAppRepository {
    suspend fun performSync()
    suspend fun getAllTransaction(): List<Transaction>
    suspend fun addTransaction(transaction: Transaction)
    suspend fun deleteTransaction(localId: String): Result<Boolean>
    suspend fun getTransactionByID(id: String): Transaction
    suspend fun updateTransaction(transaction: Transaction): Result<Boolean>
    suspend fun getSettings(): Settings
    suspend fun saveSettings(settings: Settings)
    suspend fun getCustomCategories(): Map<String, String>
    suspend fun updateSettings(settings: Settings)
}


