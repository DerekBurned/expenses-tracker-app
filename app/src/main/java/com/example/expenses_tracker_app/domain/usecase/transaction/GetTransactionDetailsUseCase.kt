package com.example.expenses_tracker_app.domain.usecase.transaction

import com.example.expenses_tracker_app.domain.model.Transaction
import com.example.expenses_tracker_app.domain.repository.IAppRepository
import javax.inject.Inject

class GetTransactionDetailsUseCase @Inject constructor(
    private val appRepository: IAppRepository
) {
    suspend operator fun invoke(id: String): Transaction {
        return appRepository.getTransactionByID(id)
    }
}
