package com.example.expenses_tracker_app.di

import android.content.Context
import androidx.room.Room
import com.example.expenses_tracker_app.data.local.dao.ExpenseDao
import com.example.expenses_tracker_app.data.local.database.RDatabase
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
    fun provideDatabase(@ApplicationContext context: Context): RDatabase {
        return Room.databaseBuilder(
            context,
            RDatabase::class.java,
            "expenses_db"
        ).build()
    }

    @Provides
    fun provideExpenseDao(database: RDatabase): ExpenseDao {
        return database.expenseDao()
    }
}