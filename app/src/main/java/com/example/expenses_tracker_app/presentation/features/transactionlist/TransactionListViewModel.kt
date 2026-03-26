package com.example.expenses_tracker_app.presentation.features.transactionlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenses_tracker_app.data.local.entity.toDomain
import com.example.expenses_tracker_app.data.local.repository.IAppLocalRepository
import com.example.expenses_tracker_app.domain.model.Transaction
import com.example.expenses_tracker_app.domain.repository.IAppRepository
import com.example.expenses_tracker_app.presentation.features.transactionlist.TransactionListContract.ViewIntent
import com.example.expenses_tracker_app.presentation.features.transactionlist.TransactionListContract.ViewEffect
import com.example.expenses_tracker_app.presentation.features.transactionlist.TransactionListContract.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
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
) : ViewModel() {

    // ── State ────────────────────────────────────────────────────────────────

    private val _viewState = MutableStateFlow(ViewState())
    val viewState: StateFlow<ViewState> = _viewState.asStateFlow()

    // ── Effects ──────────────────────────────────────────────────────────────

    /**
     * Channel with BUFFERED capacity ensures an effect is never dropped
     * even if the collector hasn't started yet (e.g. mid-recomposition).
     * Exposed as a Flow so the View can only consume, never produce.
     */
    private val _viewEffect = Channel<ViewEffect>(Channel.BUFFERED)
    val viewEffect: Flow<ViewEffect> = _viewEffect.receiveAsFlow()

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

    /** Single entry point for all user actions. */
    fun handleIntent(intent: ViewIntent) {
        when (intent) {
            is ViewIntent.AddTransactionClicked -> emitEffect(ViewEffect.NavigateToAddTransaction)
            is ViewIntent.RefreshRequested -> syncFromRemote()
            is ViewIntent.DeleteTransaction -> deleteTransaction(intent.id)
            is ViewIntent.TransactionClicked -> emitEffect(ViewEffect.NavigateToDetail(intent.id))
        }
    }

    // ── Private logic ────────────────────────────────────────────────────────

    private fun observeLocalTransactions() {
        localRepo.getAllTransaction()
            .onEach { entities ->
                val models = entities.map { it.toDomain().toUiModel() }
                // Reducer: take current state, apply the new list, return new state.
                reduce { copy(transactions = models, balance = models.sumOf { it.amount }) }
            }
            .launchIn(viewModelScope)
    }

    private fun syncFromRemote() {
        viewModelScope.launch {
            reduce { copy(isLoading = true) }
            try {
                appRepo.getAllTransaction() // internally upserts remote → Room
            } catch (e: Exception) {
                emitEffect(ViewEffect.ShowSnackbar("Sync failed: ${e.message ?: "unknown error"}"))
            } finally {
                reduce { copy(isLoading = false) }
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

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Reducer: the only place [_viewState] is ever mutated.
     * Takes the current state, applies [block] to produce the next state.
     * This is the "reduce(currentState, action) → newState" pattern.
     */
    private fun reduce(block: ViewState.() -> ViewState) {
        _viewState.update { it.block() }
    }

    private fun emitEffect(effect: ViewEffect) {
        viewModelScope.launch { _viewEffect.send(effect) }
    }
}

// ── Mapping ───────────────────────────────────────────────────────────────────

private val currencyFmt = NumberFormat.getCurrencyInstance(Locale.US)

private fun Transaction.toUiModel(): TransactionUiModel {
    val formatted = if (amount >= 0) "+${currencyFmt.format(amount)}"
    else currencyFmt.format(amount)
    return when (this) {
        is Transaction.Expense -> TransactionUiModel(
            id          = localId,
            title       = description,
            amount      = amount,
            amountLabel = formatted,
            category    = expenseType.name.replace("_", " "),
            isExpense   = true
        )
        is Transaction.Income -> TransactionUiModel(
            id          = localId,
            title       = description,
            amount      = amount,
            amountLabel = formatted,
            category    = incomeType.name.replace("_", " "),
            isExpense   = false
        )
    }
}