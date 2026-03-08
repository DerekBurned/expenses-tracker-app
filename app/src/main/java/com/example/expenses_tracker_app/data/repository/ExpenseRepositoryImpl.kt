package com.example.expenses_tracker_app.data.repository


import com.example.expenses_tracker_app.data.local.entity.toDTO
import com.example.expenses_tracker_app.data.local.repository.ExpenseLocalRepositoryImpl
import com.example.expenses_tracker_app.data.remote.ExpenseApi
import com.example.expenses_tracker_app.domain.model.Expense
import com.example.expenses_tracker_app.domain.repository.IExpenseRepository
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val localRepo: ExpenseLocalRepositoryImpl,
    private val api: ExpenseApi
): IExpenseRepository {
   override suspend fun performSync() {
        // 1. Get only unsynced items from Room
        val unsynced = localRepo.getUnsyncedExpenses()
        if (unsynced.isEmpty()) return

        // 2. Map to DTOs for the API
        val dtos = unsynced.map { entity ->
           entity.toDTO();
        }

        // 3. Push to Spring Boot
        val response = api.syncExpenses(dtos)

        // 4. If server confirms, update Room so we don't sync them again
        if (response.isSuccessful) {
            val syncedIds = unsynced.map { it.localId }
            localRepo.markAsSynced(syncedIds)
        }
    }

    override suspend fun getAllExpenses(): List<Expense> {
        TODO("Not yet implemented")
    }
}