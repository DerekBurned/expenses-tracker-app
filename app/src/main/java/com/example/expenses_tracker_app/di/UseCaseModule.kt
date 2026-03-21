package com.example.expenses_tracker_app.di

import com.example.expenses_tracker_app.domain.repository.IExpenseRepository
import com.example.expenses_tracker_app.domain.usecase.AddTransactionUseCase
import com.example.expenses_tracker_app.domain.usecase.GetAllExpensesUseCase
import com.example.expenses_tracker_app.domain.usecase.SyncAllExpensesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Fix: added provideAddTransactionUseCase().
 * Without it Hilt throws "cannot be provided without an @Provides-annotated method"
 * when injecting AddTransactionViewModel.
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetAllExpensesUseCase(repository: IExpenseRepository): GetAllExpensesUseCase =
        GetAllExpensesUseCase(repository)

    @Provides
    fun provideSyncAllExpensesUseCase(repository: IExpenseRepository): SyncAllExpensesUseCase =
        SyncAllExpensesUseCase(repository)

    @Provides
    fun provideAddTransactionUseCase(repository: IExpenseRepository): AddTransactionUseCase =
        AddTransactionUseCase(repository)
}