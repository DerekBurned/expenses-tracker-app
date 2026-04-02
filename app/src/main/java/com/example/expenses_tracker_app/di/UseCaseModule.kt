package com.example.expenses_tracker_app.di

import com.example.expenses_tracker_app.domain.repository.IAppRepository
import com.example.expenses_tracker_app.domain.usecase.transaction.AddTransactionUseCase
import com.example.expenses_tracker_app.domain.usecase.transaction.GetAllTransactionsUseCase
import com.example.expenses_tracker_app.domain.usecase.transaction.GetTransactionDetailsUseCase
import com.example.expenses_tracker_app.domain.usecase.transaction.SyncAllTransactionsUseCase
import com.example.expenses_tracker_app.domain.usecase.transaction.UpdateTransactionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetAllExpensesUseCase(repository: IAppRepository): GetAllTransactionsUseCase =
        GetAllTransactionsUseCase(repository)

    @Provides
    fun provideSyncAllExpensesUseCase(repository: IAppRepository): SyncAllTransactionsUseCase =
        SyncAllTransactionsUseCase(repository)

    @Provides
    fun provideAddTransactionUseCase(repository: IAppRepository): AddTransactionUseCase =
        AddTransactionUseCase(repository)

    @Provides
    fun provideUpdateTransactionUseCase(repository: IAppRepository): UpdateTransactionUseCase =
        UpdateTransactionUseCase(repository)

    @Provides
    fun provideGetTransactionDetailsUseCase(repository: IAppRepository): GetTransactionDetailsUseCase =
        GetTransactionDetailsUseCase(repository)
}
