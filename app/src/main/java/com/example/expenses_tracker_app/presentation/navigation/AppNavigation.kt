package com.example.expenses_tracker_app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.expenses_tracker_app.presentation.features.expense.ui.addTransaction.AddTransactionScreen
import com.example.expenses_tracker_app.presentation.features.expense.ui.transacionList.ExpenseScreen
import com.example.expenses_tracker_app.presentation.features.expense.ui.viewModels.AddTransactionViewModel
import com.example.expenses_tracker_app.presentation.features.expense.ui.viewModels.TransactionListViewModel

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
                 val vm: TransactionListViewModel = hiltViewModel()
                ExpenseScreen(
                    viewModel              = vm,
                    onNavigateToAddExpense = { navigate(AppRoute.AddExpense) }
                )
            }

            entry<AppRoute.AddExpense> {
                val vm: AddTransactionViewModel = hiltViewModel()
                AddTransactionScreen(
                    viewModel      = vm,
                    onNavigateBack = ::navigateBack
                )
            }

            entry<AppRoute.EditExpense> { /* TODO */ }

            entry<AppRoute.Settings> { /* TODO */ }

            entry<AppRoute.Detail> { /* TODO */ }
        }
    )
}