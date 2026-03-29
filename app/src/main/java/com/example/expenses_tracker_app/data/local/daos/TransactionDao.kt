package com.example.expenses_tracker_app.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.expenses_tracker_app.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    // ORDER BY date DESC — latest transaction appears first in the list
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("UPDATE transactions SET isSynced = 1 WHERE localId IN (:ids)")
    suspend fun markAsSynced(ids: List<String>)

    @Query("DELETE FROM transactions WHERE localId = :id")
    suspend fun deleteTransaction(id: String)
    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)
    @Query("SELECT * FROM transactions WHERE localId = :id")
    suspend fun getTransactionByID(id: String): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE isSynced = 0")
    suspend fun getUnsyncedTransactions(): List<TransactionEntity>
}