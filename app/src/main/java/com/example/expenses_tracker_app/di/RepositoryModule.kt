package com.example.expenses_tracker_app.di

import com.example.expenses_tracker_app.data.repository.ExpenseRepositoryImpl
import com.example.expenses_tracker_app.domain.repository.IExpenseRepository
import dagger.Binds
import javax.inject.Singleton

abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindExpenseRepository(impl: ExpenseRepositoryImpl): IExpenseRepository
}