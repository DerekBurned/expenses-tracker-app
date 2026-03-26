package com.example.expenses_tracker_app.presentation.features.transactionlist

/**
 * Single source of truth for everything the TransactionList feature
 * can show, receive, or emit. Keeping all three sealed types in one
 * file makes the feature's surface area immediately obvious.
 */
object TransactionListContract {

    /**
     * Fully immutable snapshot of the screen.
     * The View only reads this — it never mutates it.
     * [isLoading] drives the pull-to-refresh indicator only;
     * the transaction list is always shown from local Room data,
     * so there is no "blank loading" state.
     */
    data class ViewState(
        val transactions: List<TransactionUiModel> = emptyList(),
        val balance: Double = 0.0,
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )

    /**
     * Every gesture / tap / pull the user can perform on this screen.
     * The ViewModel's [handleIntent] is the only entry point — nothing
     * is called directly on the VM from the View.
     */
    sealed class ViewIntent {
        /** Tapped the FAB or "Add" button. */
        object AddTransactionClicked : ViewIntent()

        /** Manual pull-to-refresh or refresh icon tapped. */
        object RefreshRequested : ViewIntent()

        /** Swipe-to-delete or delete icon confirmed. */
        data class DeleteTransaction(val id: String) : ViewIntent()

        /** Tapped a list row (navigate to detail). */
        data class TransactionClicked(val id: String) : ViewIntent()
    }

    /**
     * One-shot side-effects that must NOT survive recomposition.
     * Emitted on a SharedFlow so they are consumed exactly once.
     */
    sealed class ViewEffect {
        object NavigateToAddTransaction : ViewEffect()
        data class NavigateToDetail(val id: String) : ViewEffect()
        data class ShowSnackbar(val message: String) : ViewEffect()
    }
}

/** Presentation model — only what the View needs, nothing more. */
data class TransactionUiModel(
    val id: String,
    val title: String,
    val amount: Double,
    val amountLabel: String,   // pre-formatted, e.g. "+$2,000.00"
    val category: String,
    val isExpense: Boolean
)