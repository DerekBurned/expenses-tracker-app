package com.example.expenses_tracker_app.presentation.features.addtransaction

import androidx.lifecycle.viewModelScope
import com.example.expenses_tracker_app.data.local.repository.IAppLocalRepository
import com.example.expenses_tracker_app.data.worker.SyncScheduler
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
    private val syncScheduler: SyncScheduler,
    private val localRepo: IAppLocalRepository
) : BaseMviViewModel<ViewState, ViewIntent, ViewEffect>(initialState = ViewState()) {

    init {
        loadCustomCategories()
    }

    override fun onIntent(intent: ViewIntent) {
        when (intent) {
            is ViewIntent.TypeToggled             -> updateState { copy(isExpense = intent.isExpense, amountError = false, descriptionError = false) }
            is ViewIntent.AmountChanged           -> updateState { copy(amountText = intent.raw.filter { it.isDigit() || it == '.' }, amountError = false) }
            is ViewIntent.DescriptionChanged      -> updateState { copy(description = intent.text, descriptionError = false) }
            is ViewIntent.ExpenseCategorySelected -> updateState { copy(selectedExpenseCategory = intent.type) }
            is ViewIntent.IncomeCategorySelected  -> updateState { copy(selectedIncomeCategory = intent.type) }
            is ViewIntent.AddCategoryClicked      -> emitEffect(ViewEffect.NavigateToAddCategory(uiState.value.isExpense))
            is ViewIntent.SubmitClicked           -> submit()
            is ViewIntent.BackClicked             -> emitEffect(ViewEffect.NavigateBack)
        }
    }

    private fun loadCustomCategories() {
        viewModelScope.launch {
            val cats = localRepo.getCustomCategories() ?: emptyMap()
            updateState { copy(customCategories = cats) }
        }
    }

    private fun submit() {
        val state = uiState.value

        val amount = state.amountText.toDoubleOrNull()
        val hasAmountError = amount == null
        val hasDescriptionError = state.description.isBlank()

        if (hasAmountError || hasDescriptionError) {
            updateState { copy(amountError = hasAmountError, descriptionError = hasDescriptionError) }
            return
        }

        if (state.isSaving) return

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

        viewModelScope.launch {
            updateState { copy(isSaving = true) }
            try {
                addTransactionUseCase(transaction)
                syncScheduler.scheduleSync()
                emitEffect(ViewEffect.NavigateBack)
            } catch (e: Exception) {
                updateState { copy(isSaving = false) }
                emitEffect(ViewEffect.ShowSnackbar(e.message ?: "Failed to save transaction"))
            }
        }
    }
}
