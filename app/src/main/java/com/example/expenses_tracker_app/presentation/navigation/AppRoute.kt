package com.example.expenses_tracker_app.presentation.navigation

sealed class AppRoute {
    data object Home : AppRoute()
    data object AddExpense : AppRoute()
    data object EditExpense : AppRoute()
    data object Settings : AppRoute()
    data class Detail(val expenseId: String) : AppRoute()
}


