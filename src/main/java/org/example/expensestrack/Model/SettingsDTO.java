package org.example.expensestrack.Model;

public class SettingsDTO {
    private String localId;
    private String name;
    private TransactionType transactionType;
    private String userId;

    public String getLocalId() { return localId; }
    public String getName() { return name; }
    public TransactionType getTransactionType() { return transactionType; }
    public String getUserId() { return userId; }
}