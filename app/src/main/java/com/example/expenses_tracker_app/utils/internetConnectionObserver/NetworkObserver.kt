package com.example.expenses_tracker_app.utils.internetConnectionObserver

import kotlinx.coroutines.flow.Flow

interface NetworkObserver {
    val isConnected: Flow<Boolean>
}