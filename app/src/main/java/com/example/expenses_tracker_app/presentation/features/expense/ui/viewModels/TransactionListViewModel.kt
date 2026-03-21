package com.example.expenses_tracker_app.presentation.features.expense.ui.viewModels

import androidx.lifecycle.viewModelScope
import com.example.expenses_tracker_app.data.local.entity.toDomain
import com.example.expenses_tracker_app.data.local.repository.AppLocalRepositoryImpl
import com.example.expenses_tracker_app.domain.model.Transaction
import com.example.expenses_tracker_app.domain.repository.IExpenseRepository
import com.example.expenses_tracker_app.presentation.features.expense.TransactionUIModel
import com.example.expenses_tracker_app.presentation.features.expense.TransactionsContract
import com.example.expenses_tracker_app.presentation.mvi.BaseMviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val repo: IExpenseRepository,
    private val localRepo: AppLocalRepositoryImpl
) : BaseMviViewModel<
        TransactionsContract.State,
        TransactionsContract.Intent,
        TransactionsContract.Effect
        >(initialState = TransactionsContract.State()) {

    init {
        // 1. Observe Room — UI updates automatically on any insert/delete.
        //    No loading state toggled here so adding a transaction is seamless.
        localRepo.getAllTransaction()
            .onEach { entities ->
                val uiModels = entities.map { entity ->
                    val domain = entity.toDomain()
                    when (domain) {
                        is Transaction.Expense -> TransactionUIModel(
                            id       = domain.localId,
                            title    = domain.description,
                            amount   = domain.amount,
                            category = domain.expenseType.name
                        )
                        is Transaction.Income -> TransactionUIModel(
                            id       = domain.localId,
                            title    = domain.description,
                            amount   = domain.amount,
                            category = domain.incomeType.name
                        )
                    }
                }
                updateState {
                    copy(
                        transactions = uiModels,
                        balance      = uiModels.sumOf { it.amount }
                    )
                }
            }
            .launchIn(viewModelScope)

        // 2. On launch, silently sync remote → Room in the background.
        //    No loading spinner — user sees local data immediately and the
        //    list refreshes automatically when Room emits the new rows.
        syncFromRemote()
        syncWithRemote()
    }

    override fun onIntent(intent: TransactionsContract.Intent) {
        when (intent) {
            is TransactionsContract.Intent.LoadTransactions ->
                syncFromRemote()
            is TransactionsContract.Intent.DeleteTransaction ->
                removeExpense(intent.id)
            is TransactionsContract.Intent.AddTransactionClicked ->
                emitEffect(TransactionsContract.Effect.NavigateToAddExpense)
            is TransactionsContract.Intent.TransactionClicked -> Unit
        }
    }

    // Silent background fetch — no loading state, no disruption to the user.
    // Room's Flow will emit automatically once remote data is upserted locally.
    private fun syncFromRemote() {
        viewModelScope.launch {
            try {
                repo.getAllTransaction() // upserts remote into Room internally
            } catch (_: Exception) {
                // Server unreachable — local data is already showing, nothing to do
            }
        }
    }
    private fun syncWithRemote() {
        viewModelScope.launch {
            try {
                repo.performSync() // upserts remote into Room internally
            } catch (_: Exception) {
                // Server unreachable — local data is already showing, nothing to do
            }
        }
    }

    private fun removeExpense(id: String) {
        viewModelScope.launch {
            repo.deleteTransaction(id)
            // Room Flow emits automatically — no manual state update needed
        }
    }
}