package com.example.expenses_tracker_app.data.local.repository

import com.example.expenses_tracker_app.data.local.entity.SettingsEntity
import com.example.expenses_tracker_app.data.local.entity.TransactionEntity
import io.realm.kotlin.query.RealmSingleQuery
import kotlinx.coroutines.flow.Flow

interface IAppLocalRepository {
    fun getAllTransaction(): Flow<List<TransactionEntity>>
    suspend fun addTransaction(expense: TransactionEntity)
    suspend fun markAsSynced(ids: List<String>)
    suspend fun deleteTransaction(id: String)
    suspend fun getSettings(): RealmSingleQuery<SettingsEntity>
    suspend fun saveSettings(settings: SettingsEntity)
    suspend fun updateSettings(settings: SettingsEntity)

    fun getUnsyncedTransactions(): List<TransactionEntity>

}