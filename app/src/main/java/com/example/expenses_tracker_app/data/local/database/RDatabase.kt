package com.example.expenses_tracker_app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.expenses_tracker_app.data.local.dao.ExpenseDao
import com.example.expenses_tracker_app.data.local.entity.ExpenseEntity

@Database(
    entities = [
        ExpenseEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class RDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
}