package com.example.expenses_tracker_app.presentation.features.expense

import androidx.lifecycle.viewModelScope
import com.example.expenses_tracker_app.domain.model.Transaction
import com.example.expenses_tracker_app.domain.repository.IExpenseRepository
import com.example.expenses_tracker_app.presentation.mvi.BaseMviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject                   // FIXED: was jakarta.inject.Inject (JEE, not Android)
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val repo: IExpenseRepository
) : BaseMviViewModel<
        ExpenseContract.State,
        ExpenseContract.Intent,
        ExpenseContract.Effect
        >(initialState = ExpenseContract.State()) {

    private val _state = MutableStateFlow(ExpenseContract.State())
    val state: StateFlow<ExpenseContract.State> = _state
        .onStart { fetchExpenses() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = ExpenseContract.State()
        )

    private val _effect = MutableSharedFlow<ExpenseContract.Effect>()
    val effect = _effect.asSharedFlow()

    fun handleIntent(intent: ExpenseContract.Intent) {
        when (intent) {
            is ExpenseContract.Intent.LoadTransactions ->
                viewModelScope.launch { fetchExpenses() }
            is ExpenseContract.Intent.DeleteTransaction ->
                removeExpense(intent.id)
            is ExpenseContract.Intent.AddTransactionClicked ->
                viewModelScope.launch { _effect.emit(ExpenseContract.Effect.NavigateToAddExpense) }
            else -> Unit
        }
    }

    private fun fetchExpenses() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val data: List<Transaction> = repo.getAllTransaction()
                val contracts = data.map { transaction ->
                    when (transaction) {
                        is Transaction.Expense -> TransactionContract(
                            id       = transaction.localId,
                            title    = transaction.description,
                            amount   = transaction.amount,
                            category = transaction.expenseType.name
                        )
                        is Transaction.Income -> TransactionContract(
                            id       = transaction.localId,
                            title    = transaction.description,
                            amount   = transaction.amount,
                            category = transaction.incomeType.name
                        )
                    }
                }
                _state.update {
                    it.copy(
                        transactions = contracts,
                        balance      = data.sumOf { t -> t.amount },
                        isLoading    = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _effect.emit(ExpenseContract.Effect.ShowError(e.message ?: "Unknown error"))
            }
        }
    }

    private fun removeExpense(id: String) {
        val updatedList = _state.value.transactions.filterNot { it.id == id }
        viewModelScope.launch { repo.deleteTransaction(id) }
        _state.update { currentState ->
            currentState.copy(
                transactions = updatedList,
                balance      = updatedList.sumOf { it.amount }
            )
        }
    }

    override fun onIntent(intent: ExpenseContract.Intent) {
        handleIntent(intent)
    }
}