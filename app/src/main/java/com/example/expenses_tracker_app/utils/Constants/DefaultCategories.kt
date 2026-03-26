package com.example.expenses_tracker_app.utils.Constants

import java.util.UUID

/**
 * Predefined custom categories seeded into [Settings.customCategories].
 * The map key format is "EXPENSE_<id>" or "INCOME_<id>" so the type
 * can always be inferred from the key alone.
 *
 * Split by transaction type so they can be merged selectively:
 *
 *   val allDefaults = DefaultCategories.EXPENSE + DefaultCategories.INCOME
 */
object DefaultCategories {

    val EXPENSE: Map<String, String> = mapOf(
        "EXPENSE_rent"          to "Rent",
        "EXPENSE_utilities"     to "Utilities",
        "EXPENSE_groceries"     to "Groceries",
        "EXPENSE_dining_out"    to "Dining Out",
        "EXPENSE_coffee"        to "Coffee",
        "EXPENSE_subscriptions" to "Subscriptions",
        "EXPENSE_travel"        to "Travel",
        "EXPENSE_fitness"       to "Fitness",
        "EXPENSE_personal_care" to "Personal Care",
        "EXPENSE_pets"          to "Pets",
        "EXPENSE_insurance"     to "Insurance",
        "EXPENSE_gifts"         to "Gifts & Donations",
        "EXPENSE_home"          to "Home & Garden",
        "EXPENSE_electronics"   to "Electronics",
        "EXPENSE_taxes"         to "Taxes",
    )

    val INCOME: Map<String, String> = mapOf(
        "INCOME_freelance"   to "Freelance",
        "INCOME_investments" to "Investments",
        "INCOME_rental"      to "Rental Income",
        "INCOME_pension"     to "Pension",
        "INCOME_cashback"    to "Cashback & Rewards",
        "INCOME_side_hustle" to "Side Hustle",
        "INCOME_dividends"   to "Dividends",
        "INCOME_tax_refund"  to "Tax Refund",
    )

    /** Combined map — use when seeding [Settings.customCategories]. */
    val ALL: Map<String, String> = EXPENSE + INCOME

    /**
     * Creates a fresh entry with a stable prefixed key, ready to be merged
     * into an existing [Settings.customCategories] map.
     *
     * Usage:
     *   val updated = settings.customCategories + DefaultCategories.newEntry("My Category", isExpense = true)
     */
    fun newEntry(name: String, isExpense: Boolean): Map<String, String> {
        val prefix = if (isExpense) "EXPENSE" else "INCOME"
        return mapOf("${prefix}_${name.lowercase()}" to name)
    }

    /** Filter helpers — use in the UI to get display names by type. */
    fun Map<String, String>.expenseNames(): List<String> =
        filterKeys { it.startsWith("EXPENSE_") }.values.toList()

    fun Map<String, String>.incomeNames(): List<String> =
        filterKeys { it.startsWith("INCOME_") }.values.toList()
}