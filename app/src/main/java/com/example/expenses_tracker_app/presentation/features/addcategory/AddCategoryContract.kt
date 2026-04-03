package com.example.expenses_tracker_app.presentation.features.addcategory

object AddCategoryContract {

    data class ViewState(
        val isExpense: Boolean = true,
        val categoryName: String = "",
        val selectedEmoji: String = "",
        val nameError: Boolean = false,
        val isSaving: Boolean = false,
        val availableEmojis: List<String> = defaultEmojis()
    )

    sealed class ViewIntent {
        data class NameChanged(val name: String) : ViewIntent()
        data class EmojiSelected(val emoji: String) : ViewIntent()
        object SaveClicked : ViewIntent()
        object BackClicked : ViewIntent()
    }

    sealed class ViewEffect {
        object NavigateBack : ViewEffect()
        data class ShowSnackbar(val message: String) : ViewEffect()
    }
}

internal fun defaultEmojis(): List<String> = listOf(
    "\uD83C\uDF54", // burger
    "\uD83D\uDE97", // car
    "\uD83C\uDFAC", // cinema
    "\uD83C\uDFE5", // hospital
    "\uD83D\uDC55", // shirt
    "\uD83C\uDF93", // graduation
    "\u2708\uFE0F", // airplane
    "\uD83C\uDFCB\uFE0F", // gym
    "\uD83D\uDCBB", // laptop
    "\uD83C\uDFE0", // house
    "\uD83D\uDED2", // shopping cart
    "\u2615",       // coffee
    "\uD83C\uDF81", // gift
    "\uD83D\uDCB0", // money bag
    "\uD83C\uDF3F", // herb/nature
    "\uD83D\uDCDA", // books
    "\uD83C\uDFB5", // music
    "\uD83D\uDC8A", // pill/medicine
    "\uD83D\uDCF1", // phone
    "\uD83C\uDF74"  // fork and knife
)
