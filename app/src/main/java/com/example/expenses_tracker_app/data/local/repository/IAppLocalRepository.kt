package com.example.expenses_tracker_app.data.local.repository

import com.example.expenses_tracker_app.data.local.entity.SettingsEntity
import com.example.expenses_tracker_app.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

interface IAppLocalRepository {
    fun getAllTransaction(): Flow<List<TransactionEntity>>
    suspend fun addTransaction(transaction: TransactionEntity)
    suspend fun markAsSynced(ids: List<String>)
    suspend fun deleteTransaction(id: String)
    suspend fun getSettings(): SettingsEntity?
    suspend fun saveSettings(settings: SettingsEntity)
    suspend fun updateSettings(settings: SettingsEntity)
    suspend fun getUnsyncedTransactions(): List<TransactionEntity>
}