package com.example.expenses_tracker_app.data.local.repository

import com.example.expenses_tracker_app.data.local.daos.SettingsDao
import com.example.expenses_tracker_app.data.local.daos.TransactionDao
import com.example.expenses_tracker_app.data.local.entity.SettingsEntity
import com.example.expenses_tracker_app.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppLocalRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val settingsDao: SettingsDao
) : IAppLocalRepository {

    override fun getAllTransaction(): Flow<List<TransactionEntity>> =
        transactionDao.getAllTransactions()

    override suspend fun addTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
    }

    override suspend fun markAsSynced(ids: List<String>) {
        transactionDao.markAsSynced(ids)
    }

    override suspend fun deleteTransaction(id: String) {
        transactionDao.deleteTransaction(id)
    }

    override suspend fun getSettings(): SettingsEntity? =
        settingsDao.getSettings()

    override suspend fun saveSettings(settings: SettingsEntity) {
        settingsDao.saveSettings(settings)
    }

    override suspend fun updateSettings(settings: SettingsEntity) {
        settingsDao.updateSettings(settings)
    }

    override suspend fun getUnsyncedTransactions(): List<TransactionEntity> =
        transactionDao.getUnsyncedTransactions()
}