package com.example.expenses_tracker_app.presentation.theme.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenses_tracker_app.data.repository.ExpenseRepositoryImpl
import kotlinx.coroutines.launch

class ExpenseViewModel(private val repository: ExpenseRepositoryImpl) : ViewModel() {

    fun syncData() {
        viewModelScope.launch {
            repository.performSync()
        }
    }
}