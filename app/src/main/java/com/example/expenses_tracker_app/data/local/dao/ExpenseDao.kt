package com.example.expenses_tracker_app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.expenses_tracker_app.data.local.entity.ExpenseEntity

@Dao
interface ExpenseDao {
@Query("SELECT * FROM expenses WHERE isSynced = 0")
fun getUnsyncedExpenses(): List<ExpenseEntity>
@Query("UPDATE expenses SET isSynced = 1 WHERE localId IN (:ids)")
fun markAsSynced(ids: List<String>)
@Query("DELETE FROM expenses WHERE localId = :id")
fun delete(id: String)
}