package com.example.expenses_tracker_app.presentation.features.expense
import com.example.expenses_tracker_app.domain.model.Transaction
interface ExpenseContract {

    data class State(
        val balance: Double = 0.0,
        val transactions: List<Transaction> = emptyList(),
        val isLoading: Boolean = false
    )

    sealed class Intent {
        object LoadTransactions : Intent()
        data class DeleteTransaction(val id: String) : Intent()
        data class TransactionClicked(val id: String) : Intent()
        object AddTransactionClicked : Intent()
    }

    sealed class Effect {
        object NavigateToAddExpense : Effect()
        data class ShowError(val message: String) : Effect()
    }
}

data class Transaction(
    val id: String,
    val title: String,
    val amount: Double,
    val category: String
)