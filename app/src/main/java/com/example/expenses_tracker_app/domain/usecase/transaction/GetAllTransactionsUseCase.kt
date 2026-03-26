package com.example.expenses_tracker_app.domain.usecase.transaction

import com.example.expenses_tracker_app.domain.model.Transaction
import com.example.expenses_tracker_app.domain.repository.IAppRepository
import javax.inject.Inject

class GetAllTransactionsUseCase @Inject constructor(
    private val appRepository: IAppRepository
) {
    // FIXED: was calling getAllExpenses() which doesn't exist on IExpenseRepository.
    // The interface method is getAllTransaction().
    suspend operator fun invoke(): List<Transaction> {
        return appRepository.getAllTransaction()
    }
}