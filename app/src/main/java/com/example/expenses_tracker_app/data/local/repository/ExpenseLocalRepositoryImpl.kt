package com.example.expenses_tracker_app.data.local.repository

import com.example.expenses_tracker_app.data.local.entity.ExpenseEntity
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExpenseLocalRepositoryImpl @Inject
constructor(
    private val realm: Realm
): IExpenseLocalRepository {

    override fun getAllExpenses(): Flow<List<ExpenseEntity>> =
        realm.query<ExpenseEntity>().asFlow()
            .map { it.list }

    override suspend fun addExpense(expense: ExpenseEntity) {
        realm.write { copyToRealm(expense) }
    }
    override suspend fun markAsSynced(ids: List<String>) {
        realm.write {
            query<ExpenseEntity>("id IN $0", ids).find().forEach { it.isSynced = true }
        }
    }
    override suspend fun deleteExpense(id: String) {
        realm.write {
            query<ExpenseEntity>("id == $0", id)
                .first().find()?.let { delete(it) }
        }
    }
    override fun getUnsyncedExpenses(): List<ExpenseEntity> =
        realm.query<ExpenseEntity>("isSynced == false").find()
    }