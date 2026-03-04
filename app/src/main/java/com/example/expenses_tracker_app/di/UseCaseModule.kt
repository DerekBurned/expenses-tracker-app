package com.example.expenses_tracker_app.di

import com.example.expenses_tracker_app.domain.repository.IExpenseRepository
import com.example.expenses_tracker_app.domain.usecase.GetAllExpensesUseCase
import com.example.expenses_tracker_app.domain.usecase.SyncAllExpensesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideGetAllExpensesUseCase(repository: IExpenseRepository): GetAllExpensesUseCase {
        return GetAllExpensesUseCase(repository)

    }

    @Provides
    fun provideSyncAllExpensesUseCase(repository: IExpenseRepository): SyncAllExpensesUseCase {
        return SyncAllExpensesUseCase(repository)

    }
}