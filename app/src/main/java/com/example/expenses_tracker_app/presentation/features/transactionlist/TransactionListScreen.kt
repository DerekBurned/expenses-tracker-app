package com.example.expenses_tracker_app.presentation.features.transactionlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expenses_tracker_app.presentation.features.TransactionUiModel
import com.example.expenses_tracker_app.presentation.features.transactionlist.TransactionListContract.ViewEffect
import com.example.expenses_tracker_app.presentation.features.transactionlist.TransactionListContract.ViewIntent
import com.example.expenses_tracker_app.presentation.features.transactionlist.TransactionListContract.ViewState

@Composable
fun TransactionListScreen(
    viewModel: TransactionListViewModel = hiltViewModel(),
    onNavigateToAdd: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

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
        onIntent          = viewModel::onIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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
                balance        = state.balance,
                isLoading      = state.isLoading,
                isFilterActive = state.filter.isActive,
                onRefresh      = { onIntent(ViewIntent.RefreshRequested) },
                onToggleFilter = { onIntent(ViewIntent.ToggleFilterVisibility) }
            )

            AnimatedVisibility(
                visible = state.isFilterVisible,
                enter   = expandVertically(),
                exit    = shrinkVertically()
            ) {
                FilterBar(
                    filter              = state.filter,
                    availableCategories = state.availableCategories,
                    onTypeChanged       = { onIntent(ViewIntent.FilterTypeChanged(it)) },
                    onCategoryChanged   = { onIntent(ViewIntent.FilterCategoryChanged(it)) },
                    onClearFilters      = { onIntent(ViewIntent.ClearFilters) }
                )
            }

            if (state.transactions.isEmpty() && !state.isLoading) {
                EmptyState(
                    modifier    = Modifier.fillMaxSize(),
                    hasFilter   = state.filter.isActive
                )
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
    isFilterActive: Boolean,
    onRefresh: () -> Unit,
    onToggleFilter: () -> Unit
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
                Text(
                    text = if (isFilterActive) "Filtered Balance" else "Balance",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text  = "$${String.format("%.2f", balance)}",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            Row {
                IconButton(onClick = onToggleFilter) {
                    Text(
                        text     = if (isFilterActive) "\uD83D\uDD0D" else "\u2699\uFE0F",
                        fontSize = 20.sp
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBar(
    filter: TransactionFilter,
    availableCategories: List<String>,
    onTypeChanged: (TransactionTypeFilter) -> Unit,
    onCategoryChanged: (String?) -> Unit,
    onClearFilters: () -> Unit
) {
    var showCategoryMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Type filter chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            TransactionTypeFilter.entries.forEach { type ->
                FilterChip(
                    selected = filter.type == type,
                    onClick  = { onTypeChanged(type) },
                    label    = {
                        Text(
                            text = when (type) {
                                TransactionTypeFilter.ALL     -> "All"
                                TransactionTypeFilter.EXPENSE -> "Expenses"
                                TransactionTypeFilter.INCOME  -> "Income"
                            },
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Category filter
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment     = Alignment.CenterVertically,
            modifier              = Modifier.fillMaxWidth()
        ) {
            Box {
                FilterChip(
                    selected = filter.category != null,
                    onClick  = { showCategoryMenu = true },
                    label    = {
                        Text(
                            text     = filter.category ?: "Category",
                            fontSize = 13.sp
                        )
                    }
                )
                DropdownMenu(
                    expanded  = showCategoryMenu,
                    onDismissRequest = { showCategoryMenu = false }
                ) {
                    DropdownMenuItem(
                        text    = { Text("All Categories") },
                        onClick = {
                            onCategoryChanged(null)
                            showCategoryMenu = false
                        }
                    )
                    availableCategories.forEach { cat ->
                        DropdownMenuItem(
                            text    = { Text(cat) },
                            onClick = {
                                onCategoryChanged(cat)
                                showCategoryMenu = false
                            }
                        )
                    }
                }
            }

            if (filter.isActive) {
                TextButton(onClick = onClearFilters) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Clear",
                        modifier           = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Clear", fontSize = 13.sp)
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
        modifier          = Modifier.clickable(onClick = onClick),
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
private fun EmptyState(
    modifier: Modifier = Modifier,
    hasFilter: Boolean = false
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text  = if (hasFilter) "No transactions match your filters."
                    else "No transactions yet.\nTap + to add one.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
