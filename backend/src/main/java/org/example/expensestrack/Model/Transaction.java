package org.example.expensestrack.Model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "expenses", indexes = {
        @Index(columnList = "expenseDate"),
        @Index(columnList = "localId", unique = true)
})
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String localId;

    private BigDecimal amount;
    private String description;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType; // INCOME or EXPENSE

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Settings settings; // ← now a full entity, not an enum

    private String userId;
    private LocalDateTime transactionDate;

    public Transaction() {}

    public Transaction(String localId, BigDecimal amount, String description,
                       TransactionType transactionType, Settings settings,
                       String userId, LocalDateTime transactionDate) {
        this.localId = localId;
        this.amount = amount;
        this.description = description;
        this.transactionType = transactionType;
        this.settings = settings;
        this.userId = userId;
        this.transactionDate = transactionDate;
    }

    public Long getId() { return id; }
    public String getLocalId() { return localId; }
    public void setLocalId(String localId) { this.localId = localId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }
    public Settings getCategory() { return settings; }
    public void setCategory(Settings settings) { this.settings = settings; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDateDate) { this.transactionDate = transactionDateDate; }
}