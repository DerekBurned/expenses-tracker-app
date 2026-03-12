package com.example.expenses_tracker_app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.expenses_tracker_app.presentation.features.expense.ExpenseViewModel
import com.example.expenses_tracker_app.presentation.features.expense.ui.ExpenseScreen

@Composable
fun AppNavigation() {
    // The back stack is just a list you own
    val backStack = remember { mutableStateListOf<AppRoute>(AppRoute.Home) }

    // Helper extensions for clean navigation calls
    fun navigate(route: AppRoute) = backStack.add(route)
    fun navigateBack() { if (backStack.size > 1) backStack.removeLastOrNull() }
    fun popToRoot()    { backStack.removeRange(1, backStack.size) }
    fun replaceTop(route: AppRoute) { backStack[backStack.lastIndex] = route }

    NavDisplay(
        backStack = backStack,
        onBack = ::navigateBack,
        entryDecorators =listOf(rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator()),
        entryProvider = entryProvider {
            entry<AppRoute.Home>{
                val vm: ExpenseViewModel = viewModel()
                ExpenseScreen(viewModel = vm)
            }
            entry<AppRoute.AddExpense>{

            }
            entry<AppRoute.EditExpense>{

            }
            entry<AppRoute.Settings>{

            }
            entry<AppRoute.Detail>{ route ->

            }

        }
    )
}