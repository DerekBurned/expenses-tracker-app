package com.example.expenses_tracker_app.domain.usecase

import com.example.expenses_tracker_app.domain.model.Transaction
import com.example.expenses_tracker_app.domain.repository.IExpenseRepository
import javax.inject.Inject

/**
 * New use case to match the addTransaction() method added to IExpenseRepository.
 * Without this, AddTransactionViewModel bypasses the domain layer by calling
 * IAppLocalRepository directly — which breaks the clean architecture boundary
 * and means sync logic (scheduling WorkManager) never fires after a save.
 *
 * The ViewModel should inject this use case instead of IAppLocalRepository.
 */
class AddTransactionUseCase @Inject constructor(
    private val expenseRepository: IExpenseRepository
) {
    suspend operator fun invoke(transaction: Transaction) {
        expenseRepository.addTransaction(transaction)
    }
}