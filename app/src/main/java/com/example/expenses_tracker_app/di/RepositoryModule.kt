package com.example.expenses_tracker_app.di

import com.example.expenses_tracker_app.data.local.repository.ExpenseLocalRepositoryImpl
import com.example.expenses_tracker_app.data.local.repository.IExpenseLocalRepository
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
    @Binds
    @Singleton
    abstract fun bindExpenseLocalRepository(impl: ExpenseLocalRepositoryImpl): IExpenseLocalRepository

}