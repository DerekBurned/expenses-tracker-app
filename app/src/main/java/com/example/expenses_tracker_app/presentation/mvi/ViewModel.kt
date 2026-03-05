package com.example.expenses_tracker_app.presentation.mvi

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface MviViewModel<State, Intent, Effect> {
    val uiState: StateFlow<State>
    val uiEffect: SharedFlow<Effect>
    fun onIntent(intent: Intent)
}