package com.example.expenses_tracker_app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.expenses_tracker_app.presentation.theme.ExpensestrackerappTheme
import com.example.expenses_tracker_app.presentation.theme.features.ExpenseContract
import com.example.expenses_tracker_app.presentation.theme.features.ExpenseViewModel
import com.example.expenses_tracker_app.presentation.theme.features.Transaction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpensestrackerappTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    val viewModel = hiltViewModel<ExpenseViewModel>()
                    val state by viewModel.state.collectAsState()
                    ExpenseContent(
                        state = state ,
                        onIntent = {}
                    )
                }
            }
        }
    }
}


@Composable
fun ExpenseScreen(viewModel: ExpenseViewModel) {
    val state by viewModel.state.collectAsState()

    // This calls the stateless version below
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
            FloatingActionButton(onClick = { onIntent(ExpenseContract.Intent.AddExpenseClicked) }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Balance Card
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(
                    text = "$${state.balance}",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // List
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
fun TransactionItem(transaction: Transaction,
                    onDeleteClick: (String) -> Unit) {
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
    // Manually creating a mock state for the preview
    val mockState = ExpenseContract.State(
        balance = 1250.50,
        transactions = listOf(
            Transaction("1", "Groceries", -85.0, "Food"),
            Transaction("2", "Salary", 2000.0, "Work"),
            Transaction("3", "Netflix", -15.99, "Subs")
        ),
        isLoading = false
    )

    MaterialTheme {
        ExpenseContent(
            state = mockState,
            onIntent = {} // Do nothing for clicks in preview
        )
    }
}