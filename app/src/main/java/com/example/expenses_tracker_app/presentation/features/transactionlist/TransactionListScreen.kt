package com.example.expenses_tracker_app.presentation.features.transactionlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expenses_tracker_app.presentation.features.TransactionUiModel
import com.example.expenses_tracker_app.presentation.features.transactionlist.TransactionListContract.ViewEffect
import com.example.expenses_tracker_app.presentation.features.transactionlist.TransactionListContract.ViewIntent
import com.example.expenses_tracker_app.presentation.features.transactionlist.TransactionListContract.ViewState

/**
 * Entry point — the only place that touches the ViewModel.
 * Collects state and wires effects. Everything below this function
 * is stateless: it receives [ViewState] and [onIntent].
 *
 * Uses [BaseMviViewModel.uiState] and [BaseMviViewModel.uiEffect] (the
 * base-class property names) instead of the old viewState/viewEffect fields.
 */
@Composable
fun TransactionListScreen(
    viewModel: TransactionListViewModel = hiltViewModel(),
    onNavigateToAdd: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    // collectAsStateWithLifecycle stops collection when the app is backgrounded,
    // avoiding wasted work and battery drain.
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // LaunchedEffect key = Unit → runs once and stays alive for the screen's
    // lifetime. Effects are one-shot: the SharedFlow with extraBufferCapacity
    // guarantees each is consumed exactly once even if recomposition occurs mid-collect.
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ViewEffect.NavigateToAddTransaction -> onNavigateToAdd()
                is ViewEffect.NavigateToDetail         -> onNavigateToDetail(effect.id)
                is ViewEffect.ShowSnackbar             -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    TransactionListContent(
        state             = state,
        snackbarHostState = snackbarHostState,
        onIntent          = viewModel::onIntent   // MviViewModel interface method
    )
}

/**
 * Stateless content composable.
 * Receives an immutable [ViewState] snapshot and an [onIntent] lambda.
 * Easy to preview and test in isolation.
 */
@Composable
fun TransactionListContent(
    state: ViewState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onIntent: (ViewIntent) -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onIntent(ViewIntent.AddTransactionClicked) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add transaction")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            BalanceCard(
                balance   = state.balance,
                isLoading = state.isLoading,
                onRefresh = { onIntent(ViewIntent.RefreshRequested) }
            )

            if (state.transactions.isEmpty() && !state.isLoading) {
                EmptyState(modifier = Modifier.fillMaxSize())
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.transactions, key = { it.id }) { item ->
                        TransactionRow(
                            item     = item,
                            onDelete = { onIntent(ViewIntent.DeleteTransaction(item.id)) },
                            onClick  = { onIntent(ViewIntent.TransactionClicked(item.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BalanceCard(
    balance: Double,
    isLoading: Boolean,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Balance", style = MaterialTheme.typography.labelMedium)
                Text(
                    text  = "$${String.format("%.2f", balance)}",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }
        }
    }
}

@Composable
private fun TransactionRow(
    item: TransactionUiModel,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent   = { Text(item.title) },
        supportingContent = { Text(item.category) },
        trailingContent   = {
            val color = if (item.isExpense) Color.Red else Color(0xFF2E7D32)
            Text(
                text       = item.amountLabel,
                color      = color,
                fontWeight = FontWeight.SemiBold
            )
        }
    )
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text  = "No transactions yet.\nTap + to add one.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}