package com.example.expenses_tracker_app.di

import com.example.expenses_tracker_app.data.repository.ExpenseRepositoryImpl
import com.example.expenses_tracker_app.domain.repository.IExpenseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindExpenseRepository(impl: ExpenseRepositoryImpl): IExpenseRepository
}