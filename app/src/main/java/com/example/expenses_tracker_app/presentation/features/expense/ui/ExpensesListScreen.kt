package com.example.expenses_tracker_app.presentation.features.expense.ui

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.expenses_tracker_app.presentation.features.expense.ExpenseContract
import com.example.expenses_tracker_app.presentation.features.expense.ExpenseViewModel
import com.example.expenses_tracker_app.presentation.features.expense.TransactionContract

@Composable
fun ExpenseScreen(viewModel: ExpenseViewModel) {
    val state by viewModel.state.collectAsState()

    ExpenseContent(
        state = state,
        onIntent = { intent -> viewModel.handleIntent(intent) }
    )
}

@Composable
fun ExpenseContent(
    state: ExpenseContract.State,
    onIntent: (ExpenseContract.Intent) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onIntent(ExpenseContract.Intent.AddTransactionClicked) }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                Text(
                    text = "$${state.balance}",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }

            LazyColumn {
                items(state.transactions) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onDeleteClick = { id -> onIntent(ExpenseContract.Intent.DeleteTransaction(id)) }
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: TransactionContract,
    onDeleteClick: (String) -> Unit
) {
    ListItem(
        headlineContent = { Text(transaction.title) },
        supportingContent = { Text(transaction.category) },
        trailingContent = {
            val color = if (transaction.amount < 0) Color.Red else Color.Green
            Text(
                text = "${if (transaction.amount > 0) "+" else ""}$${transaction.amount}",
                color = color,
                fontWeight = FontWeight.SemiBold
            )
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ExpenseScreenPreview() {
    val mockState = ExpenseContract.State(
        balance = 1250.50,
        transactions = listOf(
            TransactionContract("1", "Groceries", -85.0, "Food"),
            TransactionContract("2", "Salary", 2000.0, "Work"),
            TransactionContract("3", "Netflix", -15.99, "Subs")
        ),
        isLoading = false
    )

    MaterialTheme {
        ExpenseContent(state = mockState, onIntent = {})
    }
}