package org.example.expensestrack.Model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * NEW FILE — critical fix for the GET /api/expenses response shape mismatch.
 *
 * Problem:
 *   ExpenseController.getAll() returned List<Expense> directly. The Expense
 *   entity has a @ManyToOne Settings field named `category`. Jackson serialised
 *   this as a full nested Settings JSON object:
 *
 *     { "localId": "...", "amount": 9.99, "category": { "id": 1, "name": "FOOD", ... } }
 *
 *   Android's TransactionDTO expected a flat string field `categoryLocalId`,
 *   so Gson found no matching field and left categoryLocalId as "" — meaning
 *   every transaction came back with category DEFAULT/OTHER regardless of what
 *   was stored.
 *
 * Fix:
 *   Project Expense → ExpenseResponseDTO before serialising, so the JSON shape
 *   matches exactly what Android's TransactionDTO expects:
 *
 *     { "localId": "...", "amount": 9.99, "transactionType": "EXPENSE",
 *       "categoryLocalId": "FOOD", "description": "...", "date": "..." }
 *
 *   ExpenseController.getAll() is updated to return List<ExpenseResponseDTO>.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponseDTO {

    private String localId;
    private double amount;
    private String description;
    private String transactionType;   // "INCOME" or "EXPENSE"
    private String categoryLocalId;   // the category name string Android expects
    private String date;              // ISO date string

    public TransactionResponseDTO() {}

    /** Factory — converts a JPA Expense entity to the wire format. */
    public static TransactionResponseDTO from(Transaction transaction) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.localId          = transaction.getLocalId();
        dto.amount           = transaction.getAmount() != null
                ? transaction.getAmount().doubleValue() : 0.0;
        dto.description      = transaction.getDescription();
        dto.transactionType  = transaction.getTransactionType() != null
                ? transaction.getTransactionType().name() : "EXPENSE";
        // Resolve category name: use the Settings entity's `name` field if present,
        // otherwise fall back to the transactionType so Android can at least map it.
        dto.categoryLocalId  = transaction.getCategory() != null
                ? transaction.getCategory().getName()
                : dto.transactionType;
        dto.date             = transaction.getTransactionDate() != null
                ? transaction.getTransactionDate().toLocalDate().toString() : "";
        return dto;
    }


    public String getLocalId()         { return localId; }
    public double getAmount()          { return amount; }
    public String getDescription()     { return description; }
    public String getTransactionType() { return transactionType; }
    public String getCategoryLocalId() { return categoryLocalId; }
    public String getDate()            { return date; }
}