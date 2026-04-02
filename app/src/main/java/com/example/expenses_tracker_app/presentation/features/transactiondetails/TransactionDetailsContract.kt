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
        val Data : String  =TODO(),
        val isLoading: Boolean = false,
        val isEditDialogVisible: Boolean = false,
        val errorMessage: String = ""
    )

    sealed class ViewIntent {
        object UpdateTransactionClicked        : ViewIntent()  // opens edit dialog/sheet
        object UpdateTransactionConfirmClicked : ViewIntent()  // saves changes
        object DeleteTransactionClicked        : ViewIntent()
        object BackClicked                     : ViewIntent()
    }

    sealed class ViewEffect {
        object NavigateToTransactionList               : ViewEffect()
        data class ShowSnackbar(val message: String)   : ViewEffect()
    }
}