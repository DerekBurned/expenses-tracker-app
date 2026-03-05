package com.example.expenses_tracker_app.presentation.features.expense.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable

@Composable
fun ExpenseContent(
    state: ExpenseState,        // just data
    onIntent: (ExpenseIntent) -> Unit  // just callbacks
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onIntent(ExpenseIntent.AddExpenseClicked) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.expenses.isEmpty() -> {
                EmptyExpenseList(modifier = Modifier.padding(padding))
            }

            else -> {
                ExpenseList(
                    expenses = state.expenses,
                    totalAmount = state.totalAmount,
                    modifier = Modifier.padding(padding),
                    onExpenseClick = { id ->
                        onIntent(ExpenseIntent.ExpenseClicked(id))
                    },
                    onDeleteExpense = { id ->
                        onIntent(ExpenseIntent.DeleteExpense(id))
                    }
                )
            }
        }
    }
}