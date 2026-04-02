package com.example.expenses_tracker_app.presentation.features.transactiondetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.expenses_tracker_app.domain.model.ExpenseType
import com.example.expenses_tracker_app.domain.model.IncomeType
import com.example.expenses_tracker_app.domain.model.Transaction
import com.example.expenses_tracker_app.domain.model.toUiModel
import com.example.expenses_tracker_app.domain.repository.IAppRepository
import com.example.expenses_tracker_app.presentation.features.transactiondetails.TransactionDetailsContract.ViewEffect
import com.example.expenses_tracker_app.presentation.features.transactiondetails.TransactionDetailsContract.ViewIntent
import com.example.expenses_tracker_app.presentation.features.transactiondetails.TransactionDetailsContract.ViewState
import com.example.expenses_tracker_app.presentation.mvi.BaseMviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class TransactionDetailsViewModel @Inject constructor(
    private val appRepo: IAppRepository,
    savedStateHandle: SavedStateHandle
) : BaseMviViewModel<ViewState, ViewIntent, ViewEffect>(initialState = ViewState()) {

    private val transactionId: String = checkNotNull(savedStateHandle["transactionId"]) {
        "transactionId nav argument is missing"
    }

    private var currentTransaction: Transaction? = null

    init {
        loadTransaction()
    }

    override fun onIntent(intent: ViewIntent) {
        when (intent) {
            is ViewIntent.UpdateTransactionClicked -> {
                val tx = uiState.value.transaction
                updateState {
                    copy(
                        isEditDialogVisible = true,
                        editAmount = abs(tx.amount).toBigDecimal().toPlainString(),
                        editDescription = tx.title
                    )
                }
            }
            is ViewIntent.EditAmountChanged -> {
                val filtered = intent.raw.filter { it.isDigit() || it == '.' }
                updateState { copy(editAmount = filtered) }
            }
            is ViewIntent.EditDescriptionChanged -> updateState { copy(editDescription = intent.text) }
            is ViewIntent.DismissEditClicked -> updateState { copy(isEditDialogVisible = false) }
            is ViewIntent.UpdateTransactionConfirmClicked -> saveTransaction()
            is ViewIntent.DeleteTransactionClicked -> deleteTransaction()
            is ViewIntent.BackClicked -> emitEffect(ViewEffect.NavigateToTransactionList)
        }
    }

    private fun loadTransaction() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            try {
                val transaction = appRepo.getTransactionByID(transactionId)
                currentTransaction = transaction
                updateState { copy(isLoading = false, transaction = transaction.toUiModel()) }
            } catch (e: Exception) {
                val msg = e.message ?: "Failed to load transaction"
                updateState { copy(isLoading = false, errorMessage = msg) }
                emitEffect(ViewEffect.ShowSnackbar(msg))
            }
        }
    }

    private fun saveTransaction() {
        val current = currentTransaction ?: return
        val state = uiState.value
        val newAmount = state.editAmount.toDoubleOrNull()
        if (newAmount == null) {
            emitEffect(ViewEffect.ShowSnackbar("Invalid amount"))
            return
        }
        if (state.editDescription.isBlank()) {
            emitEffect(ViewEffect.ShowSnackbar("Description cannot be empty"))
            return
        }

        viewModelScope.launch {
            try {
                val signedAmount = if (current is Transaction.Expense) -abs(newAmount) else abs(newAmount)
                val updated = when (current) {
                    is Transaction.Expense -> current.copy(
                        amount = signedAmount,
                        description = state.editDescription.trim()
                    )
                    is Transaction.Income -> current.copy(
                        amount = signedAmount,
                        description = state.editDescription.trim()
                    )
                }
                appRepo.updateTransaction(updated)
                currentTransaction = updated
                updateState {
                    copy(
                        isEditDialogVisible = false,
                        transaction = updated.toUiModel()
                    )
                }
                emitEffect(ViewEffect.ShowSnackbar("Transaction updated"))
            } catch (e: Exception) {
                emitEffect(ViewEffect.ShowSnackbar(e.message ?: "Failed to update"))
            }
        }
    }

    private fun deleteTransaction() {
        viewModelScope.launch {
            try {
                appRepo.deleteTransaction(transactionId)
                emitEffect(ViewEffect.NavigateToTransactionList)
            } catch (e: Exception) {
                emitEffect(ViewEffect.ShowSnackbar(e.message ?: "Failed to delete transaction"))
            }
        }
    }
}
