package com.example.expenses_tracker_app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.expenses_tracker_app.data.local.dao.SettingsDao
import com.example.expenses_tracker_app.data.local.dao.TransactionDao
import com.example.expenses_tracker_app.data.local.entity.SettingsEntity
import com.example.expenses_tracker_app.data.local.entity.StringMapConverter
import com.example.expenses_tracker_app.data.local.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class, SettingsEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringMapConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun settingsDao(): SettingsDao
}