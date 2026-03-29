package com.example.expenses_tracker_app.presentation.features.transactionlist

import androidx.lifecycle.viewModelScope
import com.example.expenses_tracker_app.data.local.entity.toDomain
import com.example.expenses_tracker_app.data.local.repository.IAppLocalRepository
import com.example.expenses_tracker_app.domain.model.toUiModel
import com.example.expenses_tracker_app.domain.repository.IAppRepository
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
    /**
     * We inject the *interface* (IAppLocalRepository) here, not the
     * concrete AppLocalRepositoryImpl. This keeps the ViewModel
     * testable — swap in a fake in tests without touching Hilt.
     *
     * The Room Flow is observed here so the UI reacts to every local
     * change (add / delete / sync) automatically, without polling.
     */
    private val localRepo: IAppLocalRepository,
    private val appRepo: IAppRepository
) : BaseMviViewModel<ViewState, ViewIntent, ViewEffect>(initialState = ViewState()) {

    // ── Init ─────────────────────────────────────────────────────────────────

    init {
        // Observe Room in real-time. Because this is a Flow, the UI updates
        // automatically the moment any transaction is inserted or deleted —
        // including by the background SyncWorker.
        observeLocalTransactions()

        // On first launch, silently pull from remote → upsert into Room.
        // Room's Flow does the rest. No loading spinner here on purpose:
        // local data is shown instantly, remote data arrives in the background.
        syncFromRemote()
        syncToRemote()
    }

    // ── Intent handler ───────────────────────────────────────────────────────

    /** Single entry point for all user actions. Required by [BaseMviViewModel]. */
    override fun onIntent(intent: ViewIntent) {
        when (intent) {
            is ViewIntent.AddTransactionClicked -> emitEffect(ViewEffect.NavigateToAddTransaction)
            is ViewIntent.RefreshRequested      -> syncFromRemote()
            is ViewIntent.DeleteTransaction     -> deleteTransaction(intent.id)
            is ViewIntent.TransactionClicked    -> emitEffect(ViewEffect.NavigateToDetail(intent.id))
        }
    }

    // ── Private logic ────────────────────────────────────────────────────────

    private fun observeLocalTransactions() {
        localRepo.getAllTransaction()
            .onEach { entities ->
                val models = entities.map { it.toDomain().toUiModel() }
                // Reducer: take current state, apply the new list, return new state.
                updateState { copy(transactions = models, balance = models.sumOf { it.amount }) }
            }
            .launchIn(viewModelScope)
    }

    private fun syncFromRemote() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            try {
                appRepo.getAllTransaction() // internally upserts remote → Room
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
                // Room Flow emits automatically — no manual state update needed.
            } catch (e: Exception) {
                emitEffect(ViewEffect.ShowSnackbar("Delete failed: ${e.message ?: "unknown error"}"))
            }
        }
    }
}