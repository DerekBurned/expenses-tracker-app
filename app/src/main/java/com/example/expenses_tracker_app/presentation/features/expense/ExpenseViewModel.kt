package com.example.expenses_tracker_app.presentation.features.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenses_tracker_app.presentation.mvi.BaseMviViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExpenseViewModel : BaseMviViewModel<
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

    fun handleIntent(intent: ExpenseContract.Intent) {
        when (intent) {
            is ExpenseContract.Intent.LoadExpenses -> fetchExpenses()
            is ExpenseContract.Intent.DeleteTransaction -> removeExpense(intent.id)
            is ExpenseContract.Intent.AddExpenseClicked -> {
                viewModelScope.launch { _effect.emit(ExpenseContract.Effect.NavigateToAddExpense) }
            }
        }
    }

    private fun fetchExpenses() {
        // In a real app, get this from a Repository/API
        _state.update { it.copy(isLoading = true) }

        val mockData = listOf(
            Transaction("1", "Coffee", -4.50, "Food"),
            Transaction("2", "Salary", 2500.00, "Income"),
            Transaction("3", "Gym", -50.00, "Health")
        )

        _state.update { it.copy(
            transactions = mockData,
            balance = mockData.sumOf { t -> t.amount },
            isLoading = false
        )}
    }
    private fun removeExpense(id: String) {
        // 1. Get the current list and filter out the deleted item
        val updatedList = _state.value.transactions.filterNot { it.id == id }

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