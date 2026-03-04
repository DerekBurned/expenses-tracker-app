package com.example.expenses_tracker_app.domain.usecase

import com.example.expenses_tracker_app.domain.repository.IExpenseRepository
import javax.inject.Inject

class GetAllExpensesUseCase @Inject constructor(
    private val expenseRepository: IExpenseRepository
) {
    suspend operator fun invoke() {
        expenseRepository.getAllExpenses()

    }
}