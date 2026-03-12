package com.example.expenses_tracker_app.domain.usecase

import com.example.expenses_tracker_app.domain.model.Transaction
import com.example.expenses_tracker_app.domain.repository.IExpenseRepository
import javax.inject.Inject

class GetAllExpensesUseCase @Inject constructor(
    private val expenseRepository: IExpenseRepository
) {
    // FIXED: was calling getAllExpenses() which doesn't exist on IExpenseRepository.
    // The interface method is getAllTransaction().
    suspend operator fun invoke(): List<Transaction> {
        return expenseRepository.getAllTransaction()
    }
}