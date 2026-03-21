package com.example.expenses_tracker_app.presentation.features.expense.ui.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    // FIX: exposed as Compose state so the button reacts instantly
    // without needing a full StateFlow + collectAsState pipeline
    var isSaving by mutableStateOf(false)
        private set

    fun addTransaction(transaction: Transaction) {
        // FIX: guard — if already saving, ignore additional taps entirely
        if (isSaving) return

        viewModelScope.launch {
            isSaving = true
            try {
                addTransactionUseCase(transaction)
                syncScheduler.scheduleSync()
                // Emit NavigateBack BEFORE setting isSaving = false so the
                // screen closes immediately without re-enabling the button
                _effect.emit(Effect.NavigateBack)
            } catch (e: Exception) {
                isSaving = false
                _effect.emit(Effect.ShowError(e.message ?: "Failed to save transaction"))
            }
        }
    }
}