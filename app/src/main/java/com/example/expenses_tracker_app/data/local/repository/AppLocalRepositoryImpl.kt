package com.example.expenses_tracker_app.data.local.repository

import com.example.expenses_tracker_app.data.local.entity.SettingsEntity
import com.example.expenses_tracker_app.data.local.entity.TransactionEntity
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmSingleQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppLocalRepositoryImpl @Inject
constructor(
    private val realm: Realm
): IAppLocalRepository {

    override fun getAllTransaction(): Flow<List<TransactionEntity>> =
        realm.query<TransactionEntity>().asFlow()
            .map { it.list }

    override suspend fun addTransaction(expense: TransactionEntity) {
        realm.write { copyToRealm(expense) }
    }
    override suspend fun markAsSynced(ids: List<String>) {
        realm.write {
            query<TransactionEntity>("id IN $0", ids).find().forEach { it.isSynced = true }
        }
    }
    override suspend fun deleteTransaction(id: String) {
        realm.write {
            query<TransactionEntity>("id == $0", id)
                .first().find()?.let { delete(it) }
        }
    }

    override suspend fun getSettings(): RealmSingleQuery<SettingsEntity> {
        return  realm.query<SettingsEntity>().first()
    }

    override suspend fun saveSettings(settings: SettingsEntity) {
        realm.write { copyToRealm(settings) }
    }

    override suspend fun updateSettings(settings: SettingsEntity) {
        realm.write {
            val existingSettings = query<SettingsEntity>().first().find()
            if (existingSettings != null) {
                existingSettings.name = settings.name
                existingSettings.icon = settings.icon
                existingSettings.email = settings.email
                existingSettings.darkTheme = settings.darkTheme
                existingSettings.customCategories = settings.customCategories
            }
        }
    }

    override fun getUnsyncedTransactions(): List<TransactionEntity> =
        realm.query<TransactionEntity>("isSynced == false").find()
    }
