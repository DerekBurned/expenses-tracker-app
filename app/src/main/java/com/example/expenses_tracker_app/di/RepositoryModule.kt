package com.example.expenses_tracker_app.di

import com.example.expenses_tracker_app.data.local.repository.AppLocalRepositoryImpl
import com.example.expenses_tracker_app.data.local.repository.IAppLocalRepository
import com.example.expenses_tracker_app.data.repository.TransactionsRepositoryImpl
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
    abstract fun bindExpenseRepository(impl: TransactionsRepositoryImpl): IExpenseRepository
    @Binds
    @Singleton
    abstract fun bindExpenseLocalRepository(impl: AppLocalRepositoryImpl): IAppLocalRepository

}