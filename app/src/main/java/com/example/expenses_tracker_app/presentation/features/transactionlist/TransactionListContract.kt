package com.example.expenses_tracker_app.presentation.features.transactionlist

import com.example.expenses_tracker_app.presentation.features.TransactionUiModel

object TransactionListContract {

    data class ViewState(
        val transactions: List<TransactionUiModel> = emptyList(),
        val balance: Double = 0.0,
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val filter: TransactionFilter = TransactionFilter(),
        val availableCategories: List<String> = emptyList(),
        val isFilterVisible: Boolean = false
    )

    sealed class ViewIntent {
        object AddTransactionClicked : ViewIntent()
        object RefreshRequested : ViewIntent()
        data class DeleteTransaction(val id: String) : ViewIntent()
        data class TransactionClicked(val id: String) : ViewIntent()
        data class FilterTypeChanged(val type: TransactionTypeFilter) : ViewIntent()
        data class FilterCategoryChanged(val category: String?) : ViewIntent()
        data class FilterDateFromChanged(val date: String?) : ViewIntent()
        data class FilterDateToChanged(val date: String?) : ViewIntent()
        object ClearFilters : ViewIntent()
        object ToggleFilterVisibility : ViewIntent()
    }

    sealed class ViewEffect {
        object NavigateToAddTransaction : ViewEffect()
        data class NavigateToDetail(val id: String) : ViewEffect()
        data class ShowSnackbar(val message: String) : ViewEffect()
    }
}
