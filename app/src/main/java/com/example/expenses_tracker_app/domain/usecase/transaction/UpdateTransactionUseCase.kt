package com.example.expenses_tracker_app.domain.usecase.transaction

import com.example.expenses_tracker_app.domain.model.Transaction
import com.example.expenses_tracker_app.domain.repository.IAppRepository
import javax.inject.Inject

class UpdateTransactionUseCase @Inject constructor(
    private val appRepo: IAppRepository
) {
    suspend operator fun invoke(transaction: Transaction): Result<Boolean> {
        return appRepo.updateTransaction(transaction)
    }
}