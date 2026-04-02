package com.example.expenses_tracker_app.presentation.features.transactiondetails

import com.example.expenses_tracker_app.presentation.features.TransactionUiModel

object TransactionDetailsContract {

    data class ViewState(
        val transaction: TransactionUiModel = TransactionUiModel(
            id          = "",
            title       = "",
            category    = "",
            amount      = 0.0,
            amountLabel = "",
            isExpense   = false
        ),
        val isLoading: Boolean = false,
        val isEditDialogVisible: Boolean = false,
        val editAmount: String = "",
        val editDescription: String = "",
        val errorMessage: String = ""
    )

    sealed class ViewIntent {
        object UpdateTransactionClicked        : ViewIntent()
        object UpdateTransactionConfirmClicked : ViewIntent()
        object DismissEditClicked              : ViewIntent()
        data class EditAmountChanged(val raw: String) : ViewIntent()
        data class EditDescriptionChanged(val text: String) : ViewIntent()
        object DeleteTransactionClicked        : ViewIntent()
        object BackClicked                     : ViewIntent()
    }

    sealed class ViewEffect {
        object NavigateToTransactionList               : ViewEffect()
        data class ShowSnackbar(val message: String)   : ViewEffect()
    }
}