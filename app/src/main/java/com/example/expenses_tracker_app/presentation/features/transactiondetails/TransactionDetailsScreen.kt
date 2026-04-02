package com.example.expenses_tracker_app.presentation.features.transactiondetails

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expenses_tracker_app.presentation.features.TransactionUiModel
import com.example.expenses_tracker_app.presentation.features.transactiondetails.TransactionDetailsContract.ViewEffect
import com.example.expenses_tracker_app.presentation.features.transactiondetails.TransactionDetailsContract.ViewIntent
import com.example.expenses_tracker_app.presentation.features.transactiondetails.TransactionDetailsContract.ViewState

// ── Colour tokens (mirrors AddTransactionScreen) ──────────────────────────────

private val IncomeGreen = Color(0xFF00C896)
private val ExpenseRed  = Color(0xFFFF5C5C)
private val SurfaceDark = Color(0xFF1C1C2E)
private val CardDark    = Color(0xFF28283E)
private val DividerColor = Color(0xFF2E2E4A)
private val SubtleText  = Color(0xFF8A8AA8)
private val White       = Color(0xFFFFFFFF)

// ── Entry point ───────────────────────────────────────────────────────────────

/**
 * Injects the ViewModel, collects state, and wires one-time effects.
 * Everything below is stateless.
 */
@Composable
fun TransactionDetailsScreen(
    viewModel: TransactionDetailsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ViewEffect.NavigateToTransactionList -> onNavigateBack()
                is ViewEffect.ShowSnackbar              -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    TransactionDetailsContent(
        state             = state,
        snackbarHostState = snackbarHostState,
        onIntent          = viewModel::onIntent
    )
}

// ── Stateless content ─────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailsContent(
    state: ViewState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onIntent: (ViewIntent) -> Unit
) {
    // Local ephemeral UI state — delete confirmation dialog visibility.
    // This is purely a presentation concern; no business logic depends on it.
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val accentColor by animateColorAsState(
        targetValue   = if (state.transaction.isExpense) ExpenseRed else IncomeGreen,
        animationSpec = tween(400),
        label         = "accentColor"
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = SurfaceDark
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color    = accentColor
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                ) {
                    Spacer(Modifier.height(56.dp))

                    // ── Header ────────────────────────────────────────────────
                    Header(
                        onBack   = { onIntent(ViewIntent.BackClicked) },
                        onEdit   = { onIntent(ViewIntent.UpdateTransactionClicked) },
                        onDelete = { showDeleteConfirmation = true }
                    )

                    Spacer(Modifier.height(32.dp))

                    // ── Hero amount card ──────────────────────────────────────
                    AmountCard(
                        transaction = state.transaction,
                        accentColor = accentColor
                    )

                    Spacer(Modifier.height(24.dp))

                    // ── Detail rows ───────────────────────────────────────────
                    DetailsCard {
                        DetailRow(label = "Description", value = state.transaction.title)
                        Divider()
                        DetailRow(label = "Category",    value = state.transaction.category)
                        Divider()
                        DetailRow(label = "Date",        value = state.transaction.date)
                        Divider()
                        DetailRow(
                            label = "Type",
                            value = if (state.transaction.isExpense) "Expense" else "Income",
                            valueColor = accentColor
                        )
                    }

                    Spacer(Modifier.height(40.dp))
                }
            }
        }
    }

    // ── Edit bottom sheet ─────────────────────────────────────────────────────
    if (state.isEditDialogVisible) {
        EditBottomSheet(
            editAmount      = state.editAmount,
            editDescription = state.editDescription,
            accentColor     = accentColor,
            onAmountChange  = { onIntent(ViewIntent.EditAmountChanged(it)) },
            onDescriptionChange = { onIntent(ViewIntent.EditDescriptionChanged(it)) },
            onConfirm       = { onIntent(ViewIntent.UpdateTransactionConfirmClicked) },
            onDismiss       = { onIntent(ViewIntent.DismissEditClicked) }
        )
    }

    // ── Delete confirmation dialog ────────────────────────────────────────────
    if (showDeleteConfirmation) {
        DeleteConfirmationDialog(
            onConfirm = {
                showDeleteConfirmation = false
                onIntent(ViewIntent.DeleteTransactionClicked)
            },
            onDismiss = { showDeleteConfirmation = false }
        )
    }
}

// ── Header ────────────────────────────────────────────────────────────────────

@Composable
private fun Header(
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier          = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
        }
        Text(
            text       = "Transaction Details",
            color      = White,
            fontWeight = FontWeight.Bold,
            fontSize   = 20.sp,
            modifier   = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )
        IconButton(onClick = onEdit) {
            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = SubtleText)
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ExpenseRed)
        }
    }
}

// ── Hero amount card ──────────────────────────────────────────────────────────

@Composable
private fun AmountCard(
    transaction: TransactionUiModel,
    accentColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardDark)
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text       = if (transaction.isExpense) "Expense" else "Income",
                color      = accentColor,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text       = transaction.amountLabel,
                color      = accentColor,
                fontSize   = 42.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ── Details card ──────────────────────────────────────────────────────────────

@Composable
private fun DetailsCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
    ) {
        content()
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = White
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            text     = label,
            color    = SubtleText,
            fontSize = 14.sp
        )
        Text(
            text       = value,
            color      = valueColor,
            fontSize   = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(horizontal = 20.dp)
            .background(DividerColor)
    )
}

// ── Edit bottom sheet ─────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditBottomSheet(
    editAmount: String,
    editDescription: String,
    accentColor: Color,
    onAmountChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest  = onDismiss,
        sheetState        = sheetState,
        containerColor    = CardDark,
        dragHandle        = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(SubtleText)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp)
        ) {
            Text(
                text       = "Edit Transaction",
                color      = White,
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(24.dp))

            SheetFieldLabel("Amount")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value         = editAmount,
                onValueChange = onAmountChange,
                leadingIcon   = {
                    Text(
                        text       = "$",
                        color      = accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 18.sp,
                        modifier   = Modifier.padding(start = 12.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine      = true,
                colors          = sheetTextFieldColors(accentColor),
                shape           = RoundedCornerShape(14.dp),
                modifier        = Modifier.fillMaxWidth(),
                textStyle       = LocalTextStyle.current.copy(color = White, fontSize = 18.sp)
            )

            Spacer(Modifier.height(16.dp))

            SheetFieldLabel("Description")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value         = editDescription,
                onValueChange = onDescriptionChange,
                singleLine    = true,
                colors        = sheetTextFieldColors(accentColor),
                shape         = RoundedCornerShape(14.dp),
                modifier      = Modifier.fillMaxWidth(),
                textStyle     = LocalTextStyle.current.copy(color = White)
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick  = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape  = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor)
            ) {
                Text(
                    text       = "Save Changes",
                    color      = White,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp
                )
            }
        }
    }
}

// ── Delete confirmation dialog ────────────────────────────────────────────────

@Composable
private fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest  = onDismiss,
        containerColor    = CardDark,
        title = {
            Text(
                text       = "Delete transaction?",
                color      = White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text  = "This action cannot be undone.",
                color = SubtleText
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text       = "Delete",
                    color      = ExpenseRed,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", color = SubtleText)
            }
        }
    )
}

// ── Shared helpers ────────────────────────────────────────────────────────────

@Composable
private fun SheetFieldLabel(text: String) {
    Text(
        text       = text,
        color      = SubtleText,
        fontSize   = 12.sp,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun sheetTextFieldColors(accentColor: Color) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = accentColor,
    unfocusedBorderColor    = Color(0xFF3A3A5C),
    cursorColor             = accentColor,
    focusedLabelColor       = accentColor,
    unfocusedContainerColor = SurfaceDark,
    focusedContainerColor   = SurfaceDark
)