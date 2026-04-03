package com.example.expenses_tracker_app.presentation.features.addcategory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.expenses_tracker_app.data.local.entity.toDomain
import com.example.expenses_tracker_app.data.local.repository.IAppLocalRepository
import com.example.expenses_tracker_app.domain.model.toEntity
import com.example.expenses_tracker_app.presentation.features.addcategory.AddCategoryContract.ViewEffect
import com.example.expenses_tracker_app.presentation.features.addcategory.AddCategoryContract.ViewIntent
import com.example.expenses_tracker_app.presentation.features.addcategory.AddCategoryContract.ViewState
import com.example.expenses_tracker_app.presentation.mvi.BaseMviViewModel
import com.example.expenses_tracker_app.utils.Constants.DefaultCategories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCategoryViewModel @Inject constructor(
    private val localRepo: IAppLocalRepository,
    savedStateHandle: SavedStateHandle
) : BaseMviViewModel<ViewState, ViewIntent, ViewEffect>(
    initialState = ViewState(
        isExpense = savedStateHandle["isExpense"] ?: true
    )
) {

    override fun onIntent(intent: ViewIntent) {
        when (intent) {
            is ViewIntent.NameChanged    -> updateState { copy(categoryName = intent.name, nameError = false) }
            is ViewIntent.EmojiSelected  -> {
                val current = uiState.value.selectedEmoji
                updateState { copy(selectedEmoji = if (current == intent.emoji) "" else intent.emoji) }
            }
            is ViewIntent.SaveClicked    -> save()
            is ViewIntent.BackClicked    -> emitEffect(ViewEffect.NavigateBack)
        }
    }

    private fun save() {
        val state = uiState.value
        val name = state.categoryName.trim()

        if (name.isBlank()) {
            updateState { copy(nameError = true) }
            return
        }

        if (state.isSaving) return

        viewModelScope.launch {
            updateState { copy(isSaving = true) }
            try {
                val displayName = if (state.selectedEmoji.isNotEmpty()) {
                    "${state.selectedEmoji} $name"
                } else {
                    name
                }

                val newEntry = DefaultCategories.newEntry(name, state.isExpense)
                val currentSettings = localRepo.getSettings()
                if (currentSettings != null) {
                    val domain = currentSettings.toDomain()
                    val updated = domain.copy(
                        customCategories = domain.customCategories + newEntry.mapValues { displayName }
                    )
                    localRepo.updateSettings(updated.toEntity())
                } else {
                    val entry = newEntry.mapValues { displayName }
                    val newSettings = com.example.expenses_tracker_app.domain.model.Settings(
                        userId = "",
                        name = "",
                        icon = "",
                        email = "",
                        darkTheme = null,
                        customCategories = entry
                    )
                    localRepo.saveSettings(newSettings.toEntity())
                }

                emitEffect(ViewEffect.ShowSnackbar("Category added"))
                emitEffect(ViewEffect.NavigateBack)
            } catch (e: Exception) {
                updateState { copy(isSaving = false) }
                emitEffect(ViewEffect.ShowSnackbar(e.message ?: "Failed to save category"))
            }
        }
    }
}
