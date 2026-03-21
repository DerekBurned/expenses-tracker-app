package com.example.expenses_tracker_app.data.remote

import com.example.expenses_tracker_app.domain.model.ExpenseType
import com.example.expenses_tracker_app.domain.model.IncomeType
import com.example.expenses_tracker_app.domain.model.Transaction
import com.google.gson.annotations.SerializedName

/**
 * CRITICAL FIX: TransactionDTO was a sealed class with ExpenseDTO / IncomeDTO subtypes.
 * Gson cannot deserialize sealed classes — it has no way to know which subtype to
 * instantiate from a JSON object, so every call to GET /api/expenses threw a
 * JsonParseException (or silently returned nulls depending on Gson version).
 *
 * Solution: flatten to a single data class. The Spring server already returns a flat
 * JSON object with a `transactionType` discriminator field ("INCOME" / "EXPENSE").
 * We read that field and branch in toDomain().
 *
 * The outbound sync path (Android → Spring POST /api/expenses/sync) also used the
 * sealed subtype. Replaced with a single DTO that sets transactionType explicitly.
 *
 * All call-sites that used TransactionDTO.ExpenseDTO(...) or TransactionDTO.IncomeDTO(...)
 * are updated below in the extension functions.
 */
data class TransactionDTO(
    @SerializedName("localId")
    val localId: String = "",

    @SerializedName("amount")
    val amount: Double = 0.0,

    @SerializedName("description")
    val description: String = "",

    @SerializedName("date")
    val date: String = "",

    /**
     * Discriminator field — "INCOME" or "EXPENSE".
     * Matches the TransactionType enum on the Spring side.
     */
    @SerializedName("transactionType")
    val transactionType: String = "EXPENSE",

    /**
     * The Android-generated localId of the category (e.g. "FOOD", "SALARY").
     * On the server this is used to look up the Settings/category entity.
     * On reads the server returns the category name here (see ExpenseResponseDTO below).
     */
    @SerializedName("categoryLocalId")
    val categoryLocalId: String = ""
)

// ─── Mapping: DTO → Domain ────────────────────────────────────────────────────

fun TransactionDTO.toDomain(): Transaction =
    when (transactionType) {
        "INCOME" -> Transaction.Income(
            localId    = localId,
            amount     = amount,
            description = description,
            date       = date,
            incomeType = runCatching { IncomeType.valueOf(categoryLocalId) }
                .getOrDefault(IncomeType.OTHER)
        )
        else -> Transaction.Expense(
            localId     = localId,
            amount      = amount,
            description = description,
            date        = date,
            expenseType = runCatching { ExpenseType.valueOf(categoryLocalId) }
                .getOrDefault(ExpenseType.DEFAULT)
        )
    }