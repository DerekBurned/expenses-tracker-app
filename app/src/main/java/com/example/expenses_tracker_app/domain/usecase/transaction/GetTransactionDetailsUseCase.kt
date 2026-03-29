package com.example.expenses_tracker_app.domain.usecase.transaction

import com.example.expenses_tracker_app.data.repository.AppRepositoryImpl
import com.example.expenses_tracker_app.domain.model.Transaction
import com.example.expenses_tracker_app.domain.repository.IAppRepository
import javax.inject.Inject

class GetTransactionDetailsUseCase @Inject constructor(
    private val appRepository: IAppRepository
) {
    suspend operator fun invoke(): Transaction{
      return  appRepository.getTransactionDetails()

    }
}