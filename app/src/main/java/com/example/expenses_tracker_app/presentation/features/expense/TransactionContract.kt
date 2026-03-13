package com.example.expenses_tracker_app.presentation.features.expense

interface TransactionContract {

    data class State(
        val balance: Double = 0.0,
        val transactions: List<TransactionUIModel> = emptyList(),
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

data class TransactionUIModel(
    val id: String,
    val title: String,
    val amount: Double,
    val category: String
)