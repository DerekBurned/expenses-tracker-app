package com.example.expenses_tracker_app.presentation.features.addtransaction

import androidx.lifecycle.viewModelScope
import com.example.expenses_tracker_app.data.worker.SyncScheduler
import com.example.expenses_tracker_app.domain.model.ExpenseType
import com.example.expenses_tracker_app.domain.model.IncomeType
import com.example.expenses_tracker_app.domain.model.Transaction
import com.example.expenses_tracker_app.domain.usecase.transaction.AddTransactionUseCase
import com.example.expenses_tracker_app.presentation.features.addtransaction.AddTransactionContract.ViewEffect
import com.example.expenses_tracker_app.presentation.features.addtransaction.AddTransactionContract.ViewIntent
import com.example.expenses_tracker_app.presentation.features.addtransaction.AddTransactionContract.ViewState
import com.example.expenses_tracker_app.presentation.mvi.BaseMviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val syncScheduler: SyncScheduler
) : BaseMviViewModel<ViewState, ViewIntent, ViewEffect>(initialState = ViewState()) {

    // ── Intent handler ────────────────────────────────────────────────────────

    /**
     * Single entry point required by [BaseMviViewModel] / [MviViewModel].
     * Every user gesture sends a [ViewIntent] here.
     */
    override fun onIntent(intent: ViewIntent) {
        when (intent) {
            is ViewIntent.TypeToggled             -> updateState { copy(isExpense = intent.isExpense, amountError = false, descriptionError = false) }
            is ViewIntent.AmountChanged           -> updateState { copy(amountText = intent.raw.filter { it.isDigit() || it == '.' }, amountError = false) }
            is ViewIntent.DescriptionChanged      -> updateState { copy(description = intent.text, descriptionError = false) }
            is ViewIntent.ExpenseCategorySelected -> updateState { copy(selectedExpenseCategory = intent.type) }
            is ViewIntent.IncomeCategorySelected  -> updateState { copy(selectedIncomeCategory = intent.type) }
            is ViewIntent.AddCustomCategory       -> addCustomCategory(intent.name)
            is ViewIntent.SubmitClicked           -> submit()
            is ViewIntent.BackClicked             -> emitEffect(ViewEffect.NavigateBack)
        }
    }

    // ── Private logic ─────────────────────────────────────────────────────────

    private fun addCustomCategory(name: String) {
        val trimmed = name.trim().ifBlank { return }
        val state = uiState.value
        val prefix = if (state.isExpense) "EXPENSE" else "INCOME"
        val key = "${prefix}_${trimmed.lowercase().replace(" ", "_")}"
        updateState { copy(customCategories = customCategories + (key to trimmed)) }
    }

    private fun submit() {
        val state = uiState.value

        // ── Validation ────────────────────────────────────────────────────────
        // Surface all validation errors simultaneously (better UX, single state emission).
        val amount = state.amountText.toDoubleOrNull()
        val hasAmountError = amount == null
        val hasDescriptionError = state.description.isBlank()

        if (hasAmountError || hasDescriptionError) {
            updateState { copy(amountError = hasAmountError, descriptionError = hasDescriptionError) }
            return
        }

        // Guard: swallow duplicate taps while already saving.
        if (state.isSaving) return

        // ── Build domain model ────────────────────────────────────────────────
        val signedAmount = if (state.isExpense) -(amount!!) else amount!!
        val transaction: Transaction = if (state.isExpense) {
            Transaction.Expense(
                localId     = UUID.randomUUID().toString(),
                amount      = signedAmount,
                description = state.description.trim(),
                date        = LocalDate.now().toString(),
                expenseType = state.selectedExpenseCategory
            )
        } else {
            Transaction.Income(
                localId     = UUID.randomUUID().toString(),
                amount      = signedAmount,
                description = state.description.trim(),
                date        = LocalDate.now().toString(),
                incomeType  = state.selectedIncomeCategory
            )
        }

        // ── Persist ───────────────────────────────────────────────────────────
        viewModelScope.launch {
            updateState { copy(isSaving = true) }
            try {
                addTransactionUseCase(transaction)
                syncScheduler.scheduleSync()
                // Navigate away — isSaving never resets to false because the
                // screen will be popped. If navigation fails, the catch block handles it.
                emitEffect(ViewEffect.NavigateBack)
            } catch (e: Exception) {
                updateState { copy(isSaving = false) }
                emitEffect(ViewEffect.ShowSnackbar(e.message ?: "Failed to save transaction"))
            }
        }
    }
}