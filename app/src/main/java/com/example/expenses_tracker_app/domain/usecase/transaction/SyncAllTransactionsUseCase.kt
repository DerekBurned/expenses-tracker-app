package com.example.expenses_tracker_app.domain.usecase.transaction

import com.example.expenses_tracker_app.domain.repository.IAppRepository
import javax.inject.Inject

class SyncAllTransactionsUseCase @Inject constructor(
    private val appRepository: IAppRepository
) {
    suspend operator fun invoke() {
        appRepository.performSync()
    }
}