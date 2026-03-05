package com.example.expenses_tracker_app.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseMviViewModel<State, Intent, Effect>(
    initialState: State
) : ViewModel(), MviViewModel<State, Intent, Effect> {

    private val _uiState = MutableStateFlow(initialState)
    override val uiState: StateFlow<State> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<Effect>(
        extraBufferCapacity = 16
    )
    override val uiEffect: SharedFlow<Effect> = _uiEffect.asSharedFlow()

    // Update state — called inside ViewModel only
    protected fun updateState(reducer: State.() -> State) {
        _uiState.update { it.reducer() }
    }

    // Emit a one-time effect
    protected fun emitEffect(effect: Effect) {
        viewModelScope.launch {
            _uiEffect.emit(effect)
        }
    }
}