package com.example.expenses_tracker_app.data.repository


import com.example.expenses_tracker_app.data.local.entity.toDTO
import com.example.expenses_tracker_app.data.local.entity.toDomain
import com.example.expenses_tracker_app.data.local.repository.AppLocalRepositoryImpl
import com.example.expenses_tracker_app.data.remote.ExpenseApi
import com.example.expenses_tracker_app.data.remote.toDomain
import com.example.expenses_tracker_app.domain.model.Transaction
import com.example.expenses_tracker_app.domain.repository.IExpenseRepository
import com.example.expenses_tracker_app.utils.internetConnectionObserver.NetworkObserver
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.collections.map

class TransactionsRepositoryImpl @Inject constructor(
    private val localRepo: AppLocalRepositoryImpl,
    private val api: ExpenseApi,
    private val networkObserver: NetworkObserver
): IExpenseRepository {
    private suspend fun isOnline(): Boolean =
        networkObserver.isConnected.first()
   override suspend fun performSync() {
        val unsynced = localRepo.getUnsyncedTransactions()
        if (unsynced.isEmpty()) return

        val dtos = unsynced.map { entity ->
           entity.toDTO();
        }

        val response = api.syncExpenses(dtos)

        if (response.isSuccessful) {
            val syncedIds = unsynced.map { it.localId }
            localRepo.markAsSynced(syncedIds)
        }
    }



    override suspend fun deleteTransaction(localId: String): Result<Boolean> {
        localRepo.deleteTransaction(localId)
        api.deleteExpense(localId)
        return Result.success(true)
    }

    override suspend fun getAllTransaction(): List<Transaction> {
        return if (isOnline()){
        api.getAllExpenses().map { dto ->
            dto.toDomain()
        }
        }else {
            localRepo.getAllTransaction().first().map { it.toDomain() }
        }
    }

}