package com.example.expenses_tracker_app.data.repository

import com.example.expenses_tracker_app.data.local.entity.toDTO
import com.example.expenses_tracker_app.data.local.entity.toDomain
import com.example.expenses_tracker_app.data.local.entity.toEntity
import com.example.expenses_tracker_app.data.local.repository.AppLocalRepositoryImpl
import com.example.expenses_tracker_app.data.remote.ExpenseApi
import com.example.expenses_tracker_app.data.remote.toDomain
import com.example.expenses_tracker_app.domain.model.Transaction
import com.example.expenses_tracker_app.domain.repository.IExpenseRepository
import com.example.expenses_tracker_app.utils.internetConnectionObserver.NetworkObserver
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class TransactionsRepositoryImpl @Inject constructor(
    private val localRepo: AppLocalRepositoryImpl,
    private val api: ExpenseApi,
    private val networkObserver: NetworkObserver
) : IExpenseRepository {

    private suspend fun isOnline(): Boolean =
        networkObserver.isConnected.first()

    override suspend fun performSync() {
        val unsynced = localRepo.getUnsyncedTransactions()
        if (unsynced.isEmpty()) return
        val response = api.syncExpenses(unsynced.map { it.toDTO() })
        if (response.isSuccessful) {
            localRepo.markAsSynced(unsynced.map { it.localId })
        }
    }

    override suspend fun addTransaction(transaction: Transaction) {
        localRepo.addTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransaction(localId: String): Result<Boolean> {
        localRepo.deleteTransaction(localId)
        runCatching { api.deleteExpense(localId) }
        return Result.success(true)
    }

    override suspend fun getAllTransaction(): List<Transaction> {
        return if (isOnline()) {
            // ExpenseApi.getAllExpenses() uses the default sortBy="date" — no userId param
            // on the current interface. If you add userId to the API later, pass it here.
            api.getAllExpenses().map { it.toDomain() }
        } else {
            localRepo.getAllTransaction().first().map { it.toDomain() }
        }
    }
}