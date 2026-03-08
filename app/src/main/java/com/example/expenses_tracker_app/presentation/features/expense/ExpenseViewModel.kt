package com.example.expenses_tracker_app.presentation.features.expense

import androidx.lifecycle.viewModelScope
import com.example.expenses_tracker_app.domain.model.Transaction
import com.example.expenses_tracker_app.domain.repository.IExpenseRepository
import com.example.expenses_tracker_app.presentation.mvi.BaseMviViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExpenseViewModel @Inject constructor(
    private val repo: IExpenseRepository
) : BaseMviViewModel<
        ExpenseContract.State,
        ExpenseContract.Intent,
        ExpenseContract.Effect
        >(
            initialState = ExpenseContract.State()
        ) {

    private val _state = MutableStateFlow(ExpenseContract.State())
    val state: StateFlow<ExpenseContract.State> = _state
        .onStart { fetchExpenses() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = ExpenseContract.State() // Use the actual initial state object
        )

    private val _effect = MutableSharedFlow<ExpenseContract.Effect>()
    val effect = _effect.asSharedFlow()

    suspend fun handleIntent(intent: ExpenseContract.Intent) {
        when (intent) {
            is ExpenseContract.Intent.LoadTransactions ->
                viewModelScope.launch {  fetchExpenses() }
            is ExpenseContract.Intent.DeleteTransaction -> removeExpense(intent.id)
            is ExpenseContract.Intent.AddTransactionClicked -> {
                viewModelScope.launch { _effect.emit(ExpenseContract.Effect.NavigateToAddExpense) }
            }

            else -> {

            }
        }
    }

    private suspend fun fetchExpenses() {
        // In a real app, get this from a Repository/API
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
          val data: List<Transaction> =  repo.getAllTransaction()
            _state.update { it.copy(
                transactions = data,
                balance = data.sumOf { t -> t.amount },
                isLoading = false
            )}
        }
    }
    private fun removeExpense(id: String) {
        // 1. Get the current list and filter out the deleted item
        val updatedList = _state.value.transactions.filterNot { it.localId == id }
        viewModelScope.launch { repo.deleteTransaction(id) }
        // 2. Update the state with the new list and recalculated balance
        _state.update { currentState ->
            currentState.copy(
                transactions = updatedList,
                balance = updatedList.sumOf { it.amount }
            )
        }
}

    override fun onIntent(intent: ExpenseContract.Intent) {
        TODO("Not yet implemented")
    }
}