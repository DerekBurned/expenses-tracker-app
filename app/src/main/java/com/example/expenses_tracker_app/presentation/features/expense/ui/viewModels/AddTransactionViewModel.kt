package com.example.expenses_tracker_app.presentation.features.expense.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenses_tracker_app.data.worker.SyncScheduler
import com.example.expenses_tracker_app.domain.model.Transaction
import com.example.expenses_tracker_app.domain.usecase.AddTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Fixes applied vs the first version:
 * 1. Depends on AddTransactionUseCase (domain layer) instead of IAppLocalRepository
 *    directly — keeps the architecture boundary clean.
 * 2. Calls SyncScheduler.scheduleSync() after a successful save so WorkManager
 *    queues a background push to the Spring server when network is available.
 *    Previously a transaction saved offline would never be synced because nothing
 *    triggered the worker after insert.
 */
@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val syncScheduler: SyncScheduler
) : ViewModel() {

    sealed class Effect {
        object NavigateBack : Effect()
        data class ShowError(val message: String) : Effect()
    }

    private val _effect = MutableSharedFlow<Effect>()
    val effect = _effect.asSharedFlow()

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                addTransactionUseCase(transaction)
                syncScheduler.scheduleSync()   // kick off background sync
                _effect.emit(Effect.NavigateBack)
            } catch (e: Exception) {
                _effect.emit(Effect.ShowError(e.message ?: "Failed to save transaction"))
            }
        }
    }
}