package com.example.expenses_tracker_app.presentation.features.expense.ui.transacionList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.expenses_tracker_app.presentation.features.expense.TransactionUIModel
import com.example.expenses_tracker_app.presentation.features.expense.TransactionsContract
import com.example.expenses_tracker_app.presentation.features.expense.ui.viewModels.TransactionListViewModel

@Composable
fun ExpenseScreen(
    viewModel: com.example.expenses_tracker_app.presentation.features.expense.ui.viewModels.TransactionListViewModel,
    onNavigateToAddExpense: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is TransactionsContract.Effect.NavigateToAddExpense ->
                    onNavigateToAddExpense()
                is TransactionsContract.Effect.ShowError ->
                    snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    ExpenseContent(
        state             = state,
        snackbarHostState = snackbarHostState,
        onIntent          = viewModel::onIntent
    )
}

@Composable
fun ExpenseContent(
    state: TransactionsContract.State,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onIntent: (TransactionsContract.Intent) -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onIntent(TransactionsContract.Intent.AddTransactionClicked) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add transaction")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text     = "$${state.balance}",
                    style    = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }

            LazyColumn {
                items(state.transactions, key = { it.id }) { transaction ->
                    TransactionItem(
                        transaction   = transaction,
                        onDeleteClick = { id ->
                            onIntent(TransactionsContract.Intent.DeleteTransaction(id))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: TransactionUIModel,
    onDeleteClick: (String) -> Unit
) {
    ListItem(
        headlineContent   = { Text(transaction.title) },
        supportingContent = { Text(transaction.category) },
        trailingContent   = {
            val color = if (transaction.amount < 0) Color.Red else Color.Green
            Text(
                text       = "${if (transaction.amount > 0) "+" else ""}$${transaction.amount}",
                color      = color,
                fontWeight = FontWeight.SemiBold
            )
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ExpenseScreenPreview() {
    val mockState = TransactionsContract.State(
        balance = 1250.50,
        transactions = listOf(
            TransactionUIModel("1", "Groceries", -85.0, "FOOD"),
            TransactionUIModel("2", "Salary", 2000.0, "SALARY"),
            TransactionUIModel("3", "Netflix", -15.99, "ENTERTAINMENT")
        ),
        isLoading = false
    )
    MaterialTheme {
        ExpenseContent(state = mockState, onIntent = {})
    }
}