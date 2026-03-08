package com.example.expenses_tracker_app.data.local.config


import com.example.expenses_tracker_app.data.local.entity.ExpenseEntity
import io.realm.kotlin.RealmConfiguration

object RealmConfig {
    val configuration = RealmConfiguration.Builder(
        schema = setOf(ExpenseEntity::class)
    )
        .name("expenses.realm")
        .schemaVersion(1)
        .build()
}