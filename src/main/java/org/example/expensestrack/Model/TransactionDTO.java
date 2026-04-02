package org.example.expensestrack.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDTO {
    private String localId;
    private BigDecimal amount;
    private String description;
    private TransactionType transactionType;
    private String categoryLocalId;
    private String userId;
    private LocalDateTime transactionDate;

    public String getLocalId()             { return localId; }
    public BigDecimal getAmount()          { return amount; }
    public String getDescription()         { return description; }
    public TransactionType getTransactionType() { return transactionType; }
    public String getCategoryLocalId()     { return categoryLocalId; }

    /**
     * Android does not send userId yet. Default to "default-user" so expenses
     * are stored under a real userId and GET /api/expenses can find them.
     */
    public String getUserId() {
        return (userId != null && !userId.isBlank()) ? userId : "default-user";
    }

    public LocalDateTime getTransactionDate()  { return transactionDate; }
}