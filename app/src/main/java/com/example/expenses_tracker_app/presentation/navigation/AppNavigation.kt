package com.example.expenses_tracker_app.presentation.navigation

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.expenses_tracker_app.presentation.features.addtransaction.AddTransactionScreen
import com.example.expenses_tracker_app.presentation.features.transactionlist.TransactionListScreen

@Composable
fun AppNavigation() {
    val backStack = remember { mutableStateListOf<AppRoute>(AppRoute.Home) }

    fun navigate(route: AppRoute) = backStack.add(route)
    fun navigateBack() { if (backStack.size > 1) backStack.removeLastOrNull() }

    NavDisplay(
        backStack       = backStack,
        onBack          = ::navigateBack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {

            entry<AppRoute.Home> {
                TransactionListScreen(
                    onNavigateToAdd    = { navigate(AppRoute.AddExpense) },
                    onNavigateToDetail = { id -> navigate(AppRoute.Detail(id)) }
                )
            }

            entry<AppRoute.AddExpense> {
                AddTransactionScreen(
                    onNavigateBack = ::navigateBack
                )
            }

            entry<AppRoute.EditExpense> { /* TODO */ }
            entry<AppRoute.Settings>    { /* TODO */ }
            entry<AppRoute.Detail>      { /* TODO */ }
        }
    )
}