package com.example.expenses_tracker_app.presentation.features.transactiondetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.expenses_tracker_app.domain.model.toUiModel
import com.example.expenses_tracker_app.domain.repository.IAppRepository
import com.example.expenses_tracker_app.presentation.features.transactiondetails.TransactionDetailsContract.ViewEffect
import com.example.expenses_tracker_app.presentation.features.transactiondetails.TransactionDetailsContract.ViewIntent
import com.example.expenses_tracker_app.presentation.features.transactiondetails.TransactionDetailsContract.ViewState
import com.example.expenses_tracker_app.presentation.mvi.BaseMviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionDetailsViewModel @Inject constructor(
    private val appRepo: IAppRepository,
    savedStateHandle: SavedStateHandle
) : BaseMviViewModel<ViewState, ViewIntent, ViewEffect>(initialState = ViewState()) {

    // The transaction ID is passed as a nav argument and retrieved via
    // SavedStateHandle — no LoadTransaction intent needed.
    private val transactionId: String = checkNotNull(savedStateHandle["transactionId"]) {
        "transactionId nav argument is missing"
    }

    init {
        loadTransaction()
    }

    // ── Intent handler ────────────────────────────────────────────────────────

    override fun onIntent(intent: ViewIntent) {
        when (intent) {
            is ViewIntent.UpdateTransactionClicked        -> updateState { copy(isEditDialogVisible = true) }
            is ViewIntent.UpdateTransactionConfirmClicked -> saveTransaction()
            is ViewIntent.DeleteTransactionClicked        -> deleteTransaction()
            is ViewIntent.BackClicked                     -> emitEffect(ViewEffect.NavigateToTransactionList)
        }
    }

    // ── Private logic ─────────────────────────────────────────────────────────

    private fun loadTransaction() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            try {
                val transaction = appRepo.getTransactionByID(transactionId)
                if (transaction != null) {
                    updateState { copy(isLoading = false, transaction = transaction.toUiModel()) }
                } else {
                    val msg = "Transaction not found"
                    updateState { copy(isLoading = false, errorMessage = msg) }
                    emitEffect(ViewEffect.ShowSnackbar(msg))
                }
            } catch (e: Exception) {
                val msg = e.message ?: "Failed to load transaction"
                updateState { copy(isLoading = false, errorMessage = msg) }
                emitEffect(ViewEffect.ShowSnackbar(msg))
            }
        }
    }

    private fun saveTransaction() {
        // TODO: collect edited fields from ViewState and call appRepo.updateTransaction(...)
        // For now, close the dialog optimistically.
        updateState { copy(isEditDialogVisible = false) }
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