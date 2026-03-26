package com.example.expenses_tracker_app.presentation.features.addtransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenses_tracker_app.data.worker.SyncScheduler
import com.example.expenses_tracker_app.domain.model.ExpenseType
import com.example.expenses_tracker_app.domain.model.IncomeType
import com.example.expenses_tracker_app.domain.model.Transaction
import com.example.expenses_tracker_app.domain.usecase.transaction.AddTransactionUseCase
import com.example.expenses_tracker_app.presentation.features.addtransaction.AddTransactionContract.ViewState
import com.example.expenses_tracker_app.presentation.features.addtransaction.AddTransactionContract.ViewIntent
import com.example.expenses_tracker_app.presentation.features.addtransaction.AddTransactionContract.ViewEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val syncScheduler: SyncScheduler
) : ViewModel() {

    // ── State ─────────────────────────────────────────────────────────────────

    private val _viewState = MutableStateFlow(ViewState())
    val viewState: StateFlow<ViewState> = _viewState.asStateFlow()

    // ── Effects ───────────────────────────────────────────────────────────────

    private val _viewEffect = Channel<ViewEffect>(Channel.BUFFERED)
    val viewEffect: Flow<ViewEffect> = _viewEffect.receiveAsFlow()

    // ── Intent handler ────────────────────────────────────────────────────────

    /**
     * Single entry point. Every user gesture sends a [ViewIntent] here.
     * No public methods exist on the ViewModel other than this one —
     * this enforces UDF strictly.
     */
    fun handleIntent(intent: ViewIntent) {
        when (intent) {
            is ViewIntent.TypeToggled            -> reduce { copy(isExpense = intent.isExpense, amountError = false, descriptionError = false) }
            is ViewIntent.AmountChanged          -> reduce { copy(amountText = intent.raw.filter { it.isDigit() || it == '.' }, amountError = false) }
            is ViewIntent.DescriptionChanged     -> reduce { copy(description = intent.text, descriptionError = false) }
            is ViewIntent.ExpenseCategorySelected -> reduce { copy(selectedExpenseCategory = intent.type) }
            is ViewIntent.IncomeCategorySelected  -> reduce { copy(selectedIncomeCategory = intent.type) }
            is ViewIntent.AddCustomCategory      -> addCustomCategory(intent.name)
            is ViewIntent.SubmitClicked          -> submit()
            is ViewIntent.BackClicked            -> emitEffect(ViewEffect.NavigateBack)
        }
    }

    // ── Private logic ─────────────────────────────────────────────────────────

    private fun addCustomCategory(name: String) {
        val trimmed = name.trim().ifBlank { return }
        val state = _viewState.value
        val prefix = if (state.isExpense) "EXPENSE" else "INCOME"
        val key = "${prefix}_${trimmed.lowercase().replace(" ", "_")}"
        reduce { copy(customCategories = customCategories + (key to trimmed)) }
    }

    private fun submit() {
        val state = _viewState.value

        // ── Validation ────────────────────────────────────────────────────────
        // Produce a new state that surfaces all validation errors simultaneously
        // rather than one-at-a-time (better UX, single state emission).
        val amount = state.amountText.toDoubleOrNull()
        val hasAmountError = amount == null
        val hasDescriptionError = state.description.isBlank()

        if (hasAmountError || hasDescriptionError) {
            reduce { copy(amountError = hasAmountError, descriptionError = hasDescriptionError) }
            return
        }

        // Guard: if already saving, swallow duplicate taps.
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
            reduce { copy(isSaving = true) }
            try {
                addTransactionUseCase(transaction)
                syncScheduler.scheduleSync()
                // Navigate away — isSaving never resets to false because the
                // screen will be popped. If navigation fails, the catch block handles it.
                emitEffect(ViewEffect.NavigateBack)
            } catch (e: Exception) {
                reduce { copy(isSaving = false) }
                emitEffect(ViewEffect.ShowSnackbar(e.message ?: "Failed to save transaction"))
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun reduce(block: ViewState.() -> ViewState) {
        _viewState.update { it.block() }
    }

    private fun emitEffect(effect: ViewEffect) {
        viewModelScope.launch { _viewEffect.send(effect) }
    }
}