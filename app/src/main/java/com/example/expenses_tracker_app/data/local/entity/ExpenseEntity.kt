package com.example.expenses_tracker_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val localId: String = UUID.randomUUID().toString(),
    val amount: Double,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)