package com.example.expenses_tracker_app.data.local.config


import com.example.expenses_tracker_app.data.local.entity.SettingsEntity
import com.example.expenses_tracker_app.data.local.entity.TransactionEntity
import com.example.expenses_tracker_app.presentation.features.expense.ui.TransactionItem
import io.realm.kotlin.RealmConfiguration

object RealmConfig {
    val configuration = RealmConfiguration.Builder(
        schema = setOf(TransactionEntity::class,
            SettingsEntity::class)
    )
        .name("expenses.realm")
        .schemaVersion(2)
        .build()
}