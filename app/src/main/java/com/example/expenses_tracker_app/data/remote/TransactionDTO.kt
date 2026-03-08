package com.example.expenses_tracker_app.data.remote

import com.google.gson.annotations.SerializedName

sealed class TransactionDTO{
   abstract val localId: String
   abstract val amount: Double
   abstract val description: String
   abstract val date: String


    data class ExpenseDTO(
        override val localId: String,
        override val amount: Double,
        override val description: String,
        override val date: String,
        @SerializedName("transactionType") val transactionType: String = "EXPENSE",
        @SerializedName("category") val category: String? = "FOOD"  // ← add this
    ) : TransactionDTO()

    data class IncomeDTO(
        override val localId: String,
        override val amount: Double,
        override val description: String,
        override val date: String,
        @SerializedName("transactionType") val transactionType: String = "INCOME",  // ← fix this
        @SerializedName("categoryLocalId") val categoryLocalId: String  // ← add this
    ) : TransactionDTO()


}


