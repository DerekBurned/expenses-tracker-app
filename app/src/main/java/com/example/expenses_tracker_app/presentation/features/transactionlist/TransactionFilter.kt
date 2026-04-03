
package com.example.expenses_tracker_app.presentation.features.transactionlist

data class TransactionFilter(
    val type: TransactionTypeFilter = TransactionTypeFilter.ALL,
    val category: String? = null,
    val dateFrom: String? = null,
    val dateTo: String? = null
) {
    val isActive: Boolean
        get() = type != TransactionTypeFilter.ALL || category != null || dateFrom != null || dateTo != null
}

enum class TransactionTypeFilter { ALL, EXPENSE, INCOME }
