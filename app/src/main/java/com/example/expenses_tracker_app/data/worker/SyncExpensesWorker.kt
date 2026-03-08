package com.example.expenses_tracker_app.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.expenses_tracker_app.data.local.entity.toDTO
import com.example.expenses_tracker_app.data.local.repository.IAppLocalRepository
import com.example.expenses_tracker_app.data.remote.ExpenseApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.collections.map

@HiltWorker
class SyncExpensesWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val localRepository: IAppLocalRepository,
    private val api: ExpenseApi
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val unsynced = localRepository.getUnsyncedTransactions()

            if (unsynced.isEmpty()) return Result.success()

            val dtos = unsynced.map { it.toDTO() }
            api.syncExpenses(dtos)

            val ids = unsynced.map { it.localId }
            localRepository.markAsSynced(ids)

            Result.success()
        } catch (e: Exception) {
            Result.retry() // WorkManager will retry automatically
        }
    }
}