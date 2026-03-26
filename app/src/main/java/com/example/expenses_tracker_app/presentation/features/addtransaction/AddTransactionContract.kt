package com.example.expenses_tracker_app.presentation.features.addtransaction

import com.example.expenses_tracker_app.domain.model.ExpenseType
import com.example.expenses_tracker_app.domain.model.IncomeType

object AddTransactionContract {

    /**
     * Every piece of form state lives here, not in the Composable.
     * This survives configuration changes and process death (via SavedStateHandle
     * if needed later). The View is purely a render of this snapshot.
     *
     * [customCategories] starts empty; it will be loaded from settings
     * in the ViewModel's init block.
     */
    data class ViewState(
        val isExpense: Boolean = true,
        val amountText: String = "",
        val description: String = "",
        val selectedExpenseCategory: ExpenseType = ExpenseType.DEFAULT,
        val selectedIncomeCategory: IncomeType = IncomeType.OTHER,
        val customCategories: Map<String, String> = emptyMap(),
        val isSaving: Boolean = false,
        val amountError: Boolean = false,
        val descriptionError: Boolean = false
    )

    sealed class ViewIntent {
        data class TypeToggled(val isExpense: Boolean) : ViewIntent()
        data class AmountChanged(val raw: String) : ViewIntent()
        data class DescriptionChanged(val text: String) : ViewIntent()
        data class ExpenseCategorySelected(val type: ExpenseType) : ViewIntent()
        data class IncomeCategorySelected(val type: IncomeType) : ViewIntent()
        data class AddCustomCategory(val name: String) : ViewIntent()
        object SubmitClicked : ViewIntent()
        object BackClicked : ViewIntent()
    }

    sealed class ViewEffect {
        object NavigateBack : ViewEffect()
        data class ShowSnackbar(val message: String) : ViewEffect()
    }
}