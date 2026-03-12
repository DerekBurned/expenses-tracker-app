package com.example.expenses_tracker_app.data.worker

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import jakarta.inject.Inject
import java.util.concurrent.TimeUnit

class SyncScheduler @Inject constructor(
    private val workManager: WorkManager
) {
    fun scheduleSync() {
        val request = OneTimeWorkRequestBuilder<SyncExpensesWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED) // only run when online
                    .build()
            )
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                15, TimeUnit.MINUTES        // retry after each 15m
            )
            .build()

        workManager.enqueueUniqueWork(
            "sync_expenses",
            ExistingWorkPolicy.KEEP,        // don't queue duplicates
            request
        )
    }
}