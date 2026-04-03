package com.example.expenses_tracker_app.presentation.features.transactionlist

import androidx.lifecycle.viewModelScope
import com.example.expenses_tracker_app.data.local.entity.toDomain
import com.example.expenses_tracker_app.data.local.repository.IAppLocalRepository
import com.example.expenses_tracker_app.domain.model.toUiModel
import com.example.expenses_tracker_app.domain.repository.IAppRepository
import com.example.expenses_tracker_app.presentation.features.TransactionUiModel
import com.example.expenses_tracker_app.presentation.features.transactionlist.TransactionListContract.ViewEffect
import com.example.expenses_tracker_app.presentation.features.transactionlist.TransactionListContract.ViewIntent
import com.example.expenses_tracker_app.presentation.features.transactionlist.TransactionListContract.ViewState
import com.example.expenses_tracker_app.presentation.mvi.BaseMviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val localRepo: IAppLocalRepository,
    private val appRepo: IAppRepository
) : BaseMviViewModel<ViewState, ViewIntent, ViewEffect>(initialState = ViewState()) {

    private var allTransactions: List<TransactionUiModel> = emptyList()

    init {
        observeLocalTransactions()
        syncFromRemote()
        syncToRemote()
    }

    override fun onIntent(intent: ViewIntent) {
        when (intent) {
            is ViewIntent.AddTransactionClicked   -> emitEffect(ViewEffect.NavigateToAddTransaction)
            is ViewIntent.RefreshRequested        -> syncFromRemote()
            is ViewIntent.DeleteTransaction       -> deleteTransaction(intent.id)
            is ViewIntent.TransactionClicked      -> emitEffect(ViewEffect.NavigateToDetail(intent.id))
            is ViewIntent.FilterTypeChanged       -> updateFilter { copy(type = intent.type) }
            is ViewIntent.FilterCategoryChanged   -> updateFilter { copy(category = intent.category) }
            is ViewIntent.FilterDateFromChanged   -> updateFilter { copy(dateFrom = intent.date) }
            is ViewIntent.FilterDateToChanged     -> updateFilter { copy(dateTo = intent.date) }
            is ViewIntent.ClearFilters            -> updateFilter { TransactionFilter() }
            is ViewIntent.ToggleFilterVisibility  -> updateState { copy(isFilterVisible = !isFilterVisible) }
        }
    }

    private fun updateFilter(reducer: TransactionFilter.() -> TransactionFilter) {
        val newFilter = uiState.value.filter.reducer()
        val filtered = applyFilter(allTransactions, newFilter)
        updateState {
            copy(
                filter = newFilter,
                transactions = filtered,
                balance = filtered.sumOf { it.amount }
            )
        }
    }

    private fun observeLocalTransactions() {
        localRepo.getAllTransaction()
            .onEach { entities ->
                val models = entities.map { it.toDomain().toUiModel() }
                allTransactions = models

                val categories = models.map { it.category }.distinct().sorted()
                val filtered = applyFilter(models, uiState.value.filter)

                updateState {
                    copy(
                        transactions = filtered,
                        balance = filtered.sumOf { it.amount },
                        availableCategories = categories
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun applyFilter(
        list: List<TransactionUiModel>,
        filter: TransactionFilter
    ): List<TransactionUiModel> {
        var result = list

        when (filter.type) {
            TransactionTypeFilter.EXPENSE -> result = result.filter { it.isExpense }
            TransactionTypeFilter.INCOME  -> result = result.filter { !it.isExpense }
            TransactionTypeFilter.ALL     -> { /* no-op */ }
        }

        filter.category?.let { cat ->
            result = result.filter { it.category.equals(cat, ignoreCase = true) }
        }

        filter.dateFrom?.let { from ->
            result = result.filter { it.date >= from }
        }

        filter.dateTo?.let { to ->
            result = result.filter { it.date <= to }
        }

        return result
    }

    private fun syncFromRemote() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            try {
                appRepo.getAllTransaction()
            } catch (e: Exception) {
                emitEffect(ViewEffect.ShowSnackbar("Sync failed: ${e.message ?: "unknown error"}"))
            } finally {
                updateState { copy(isLoading = false) }
            }
        }
    }

    private fun syncToRemote() {
        viewModelScope.launch {
            try { appRepo.performSync() } catch (_: Exception) { }
        }
    }

    private fun deleteTransaction(id: String) {
        viewModelScope.launch {
            try {
                appRepo.deleteTransaction(id)
            } catch (e: Exception) {
                emitEffect(ViewEffect.ShowSnackbar("Delete failed: ${e.message ?: "unknown error"}"))
            }
        }
    }
}
