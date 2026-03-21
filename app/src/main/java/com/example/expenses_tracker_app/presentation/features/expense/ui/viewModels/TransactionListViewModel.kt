package com.example.expenses_tracker_app.presentation.features.expense.ui.viewModels

import androidx.lifecycle.viewModelScope
import com.example.expenses_tracker_app.domain.model.Transaction
import com.example.expenses_tracker_app.domain.repository.IExpenseRepository
import com.example.expenses_tracker_app.presentation.features.expense.TransactionUIModel
import com.example.expenses_tracker_app.presentation.features.expense.TransactionsContract
import com.example.expenses_tracker_app.presentation.mvi.BaseMviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val repo: IExpenseRepository
) : BaseMviViewModel<
        TransactionsContract.State,
        TransactionsContract.Intent,
        TransactionsContract.Effect
        >(initialState = TransactionsContract.State()) {

    init {
        fetchExpenses()
    }

    override fun onIntent(intent: TransactionsContract.Intent) {
        when (intent) {
            is TransactionsContract.Intent.LoadTransactions ->
                fetchExpenses()
            is TransactionsContract.Intent.DeleteTransaction ->
                removeExpense(intent.id)
            is TransactionsContract.Intent.AddTransactionClicked ->
                emitEffect(TransactionsContract.Effect.NavigateToAddExpense)
            is TransactionsContract.Intent.TransactionClicked -> Unit
        }
    }

    private fun fetchExpenses() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            try {
                val data: List<Transaction> = repo.getAllTransaction()
                val uiModels = data.map { transaction ->
                    when (transaction) {
                        is Transaction.Expense -> TransactionUIModel(
                            id       = transaction.localId,
                            title    = transaction.description,
                            amount   = transaction.amount,
                            category = transaction.expenseType.name
                        )
                        is Transaction.Income -> TransactionUIModel(
                            id       = transaction.localId,
                            title    = transaction.description,
                            amount   = transaction.amount,
                            category = transaction.incomeType.name
                        )
                    }
                }
                updateState {
                    copy(
                        transactions = uiModels,
                        balance      = data.sumOf { it.amount },
                        isLoading    = false
                    )
                }
            } catch (e: Exception) {
                updateState { copy(isLoading = false) }
                emitEffect(TransactionsContract.Effect.ShowError(e.message ?: "Unknown error"))
            }
        }
    }

    private fun removeExpense(id: String) {
        val updatedList = uiState.value.transactions.filterNot { it.id == id }
        updateState {
            copy(
                transactions = updatedList,
                balance      = updatedList.sumOf { it.amount }
            )
        }
        viewModelScope.launch { repo.deleteTransaction(id) }
    }
}