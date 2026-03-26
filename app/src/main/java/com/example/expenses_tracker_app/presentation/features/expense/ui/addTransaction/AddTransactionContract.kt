package com.example.expenses_tracker_app.presentation.features.expense.ui.addTransaction

sealed class AddTransactionContract {
    data class State(
        val isExpense: Boolean = true,
    )

    sealed class Intent {
        data class DeleteCategory(val id: String) : Intent()
        data class CategoryClicked(val id: String) : Intent()
        object AddCategoryClicked : Intent()
    }

    sealed class Effect {
        object NavigateToAddCategory : Effect()
        data class ShowError(val message: String) : Effect()
    }
}


