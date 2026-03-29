package com.example.expenses_tracker_app.data.repository

import coil3.network.NetworkResponseBody
import com.example.expenses_tracker_app.data.local.entity.toDTO
import com.example.expenses_tracker_app.data.local.entity.toDomain
import com.example.expenses_tracker_app.data.local.entity.toEntity
import com.example.expenses_tracker_app.data.local.repository.AppLocalRepositoryImpl
import com.example.expenses_tracker_app.data.remote.ExpenseApi
import com.example.expenses_tracker_app.data.remote.toEntity
import com.example.expenses_tracker_app.domain.model.Settings
import com.example.expenses_tracker_app.domain.model.Transaction
import com.example.expenses_tracker_app.domain.repository.IAppRepository
import com.example.expenses_tracker_app.utils.internetConnectionObserver.NetworkObserver
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    private val localRepo: AppLocalRepositoryImpl,
    private val api: ExpenseApi,
    private val networkObserver: NetworkObserver
) : IAppRepository {

    private suspend fun isOnline(): Boolean =
        networkObserver.isConnected.first()

    /**
     * Room is always the source of truth.
     *
     * Flow:
     * 1. Always return data from Room — the UI always has something to show.
     * 2. If online, try to fetch from server and upsert into Room so remote
     *    data (added from another device) also appears locally.
     * 3. If offline or server unreachable — Room still has everything the user
     *    added locally, so the list is never empty just because of no network.
     */
    override suspend fun getAllTransaction(): List<Transaction> {
        if (isOnline()) {
            try {
                val remoteList = api.getAllExpenses()
                // Upsert each remote transaction into Room (REPLACE on conflict)
                remoteList.forEach { dto ->
                    localRepo.addTransaction(dto.toEntity())
                }
            } catch (_: Exception) {
                // Server unreachable — silently fall through to local read below
            }
        }
        // Always read from Room — works online, offline, and when server is down
        return localRepo.getAllTransaction().first().map { it.toDomain() }
    }

    override suspend fun addTransaction(transaction: Transaction) {
        // Save to Room immediately — visible in UI right away
        // SyncExpensesWorker will push to server in background when connected
        localRepo.addTransaction(transaction.toEntity())
    }

    override suspend fun performSync() {
        val unsynced = localRepo.getUnsyncedTransactions()
        if (unsynced.isEmpty()) return
        try {
            val response = api.syncExpenses(unsynced.map { it.toDTO() })
            if (response.isSuccessful) {
                localRepo.markAsSynced(unsynced.map { it.localId })
            }
        } catch (_: Exception) {
            // Will retry next time scheduleSync() fires
        }
    }

    override suspend fun deleteTransaction(localId: String): Result<Boolean> {
        if (localRepo.getTransactionById(localId) != null) {
            if (api.checkIfTransactionExists(localId).body() == true) {
                api.deleteTransaction(localId)
            }
            localRepo.deleteTransaction(localId)
            return Result.success(true)
        }
        return Result.failure(Exception("Transaction not found"))

    }

    override suspend fun getTransactionByID(id: String): Transaction {
        if (localRepo.getTransactionById(id) != null) {
            return localRepo.getTransactionById(id)!!.toDomain()
        }
        return api.getTransactionByID(id).toEntity().toDomain().also {
            localRepo.addTransaction(it.toEntity())
            if (localRepo.getTransactionById(id)?.isSynced == false) {
                try {
                    api.updateTransaction(id, it.toEntity().toDTO())
                } catch (_: Exception) {
                    // silently fail
                }

            }


        }
    }

    override suspend fun updateTransaction(transaction: Transaction): Result<Boolean> {
        localRepo.updateTransaction(transaction.toEntity())
        if (api.checkIfTransactionExists(transaction.localId).body() == true) {
            try {
                api.updateTransaction(transaction.localId, transaction.toEntity().toDTO())
            } catch (_: Exception) {

            }
        }
        return Result.success(true)
    }

    override suspend fun getSettings(): Settings {
        if (isOnline()) {
            try {
                api.getSettings().let { dto ->
                    localRepo.saveSettings(dto.toEntity())
                }
            } catch (_: Exception) {
            }
        }
        return localRepo.getSettings()?.toDomain() ?: TODO()
    }

    override suspend fun saveSettings(settings: Settings) {
        TODO("Not yet implemented")
    }

    override suspend fun getCustomCategories(): Map<String, String> {
        TODO("Not yet implemented")
    }

    override suspend fun updateSettings(settings: Settings) {
        TODO("Not yet implemented")
    }

}