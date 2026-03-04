package com.example.expenses_tracker_app.data.repository


import com.example.expenses_tracker_app.data.local.dao.ExpenseDao
import com.example.expenses_tracker_app.data.remote.ExpenseApi
import com.example.expenses_tracker_app.data.remote.ExpenseDto
import com.example.expenses_tracker_app.domain.model.Expense
import com.example.expenses_tracker_app.domain.repository.IExpenseRepository
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val dao: ExpenseDao,
    private val api: ExpenseApi
): IExpenseRepository {
   override suspend fun performSync() {
        // 1. Get only unsynced items from Room
        val unsynced = dao.getUnsyncedExpenses()
        if (unsynced.isEmpty()) return

        // 2. Map to DTOs for the API
        val dtos = unsynced.map { entity ->
            ExpenseDto(
                localId = entity.localId,
                amount = entity.amount,
                description = entity.description,
                expenseDate = Instant.ofEpochMilli(entity.timestamp)
                    .atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
        }

        // 3. Push to Spring Boot
        val response = api.syncExpenses(dtos)

        // 4. If server confirms, update Room so we don't sync them again
        if (response.isSuccessful) {
            val syncedIds = unsynced.map { it.localId }
            dao.markAsSynced(syncedIds)
        }
    }

    override suspend fun getAllExpenses(): List<Expense> {
        TODO("Not yet implemented")
    }
}