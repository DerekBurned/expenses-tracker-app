package com.example.expenses_tracker_app.di

import android.content.Context
import androidx.room.Room
import com.example.expenses_tracker_app.data.local.database.AppDatabase
import com.example.expenses_tracker_app.data.local.daos.SettingsDao
import com.example.expenses_tracker_app.data.local.daos.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "expenses.db"
        ).build()

    @Provides
    @Singleton
    fun provideTransactionDao(db: AppDatabase): TransactionDao =
        db.transactionDao()

    @Provides
    @Singleton
    fun provideSettingsDao(db: AppDatabase): SettingsDao =
        db.settingsDao()
}