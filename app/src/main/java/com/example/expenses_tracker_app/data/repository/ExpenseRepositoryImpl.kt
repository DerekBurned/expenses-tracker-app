package com.example.expenses_tracker_app.data.repository


import com.example.expenses_tracker_app.data.local.entity.toDTO
import com.example.expenses_tracker_app.data.local.entity.toDomain
import com.example.expenses_tracker_app.data.local.repository.ExpenseLocalRepositoryImpl
import com.example.expenses_tracker_app.data.remote.ExpenseApi
import com.example.expenses_tracker_app.data.remote.toDomain
import com.example.expenses_tracker_app.domain.model.Transaction
import com.example.expenses_tracker_app.domain.repository.IExpenseRepository
import com.example.expenses_tracker_app.utils.internetConnectionObserver.NetworkObserver
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val localRepo: ExpenseLocalRepositoryImpl,
    private val api: ExpenseApi,
    private val networkObserver: NetworkObserver
): IExpenseRepository {
    private suspend fun isOnline(): Boolean =
        networkObserver.isConnected.first()
   override suspend fun performSync() {
        val unsynced = localRepo.getUnsyncedExpenses()
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

    override suspend fun getAllExpenses(): List<Transaction> {
        return if (isOnline()){
        val dtos = api.getAllExpenses()
          dtos.map { dto ->
            dto.toDomain()
        }
        }else {
            localRepo.getAllExpenses().first().map { it.toDomain() }
        }
    }

}