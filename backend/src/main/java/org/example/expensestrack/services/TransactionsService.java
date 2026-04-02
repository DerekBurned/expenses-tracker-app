package org.example.expensestrack.services;

import org.example.expensestrack.Model.Transaction;
import org.example.expensestrack.Model.TransactionDTO;
import org.example.expensestrack.Model.Settings;
import org.example.expensestrack.repository.TransactionsRepository;
import org.example.expensestrack.repository.SettingsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionsService {

    private final TransactionsRepository repository;
    private final SettingsRepository categoryRepository;

    public TransactionsService(TransactionsRepository repository, SettingsRepository categoryRepository) {
        this.repository         = repository;
        this.categoryRepository = categoryRepository;
    }

    public int syncOfflineTransactions(List<TransactionDTO> dtos) {
        List<Transaction> toSave = new ArrayList<>();

        for (TransactionDTO dto : dtos) {
            if (!repository.existsByLocalId(dto.getLocalId())) {

                Settings category = categoryRepository
                        .findByLocalId(dto.getCategoryLocalId())
                        .orElse(null);

                // Fall back to now() if the client sent no date
                LocalDateTime date = dto.getTransactionDate() != null
                        ? dto.getTransactionDate()
                        : LocalDateTime.now();

                toSave.add(new Transaction(
                        dto.getLocalId(),
                        dto.getAmount(),
                        dto.getDescription(),
                        dto.getTransactionType(),
                        category,
                        dto.getUserId(),
                        date
                ));
            }
        }

        repository.saveAll(toSave);
        return toSave.size();
    }

    // deleteByLocalId() is now void — check existence first, then delete.
    public boolean deleteExpenseById(String localId) {
        if (!repository.existsByLocalId(localId)) return false;
        repository.deleteByLocalId(localId);
        return true;
    }
    public Transaction getExpenseById(String localId) {
        if (!repository.existsByLocalId(localId)) return null;
        return  repository.getTransactionByLocalId(localId);
    }

    public List<Transaction> getAllExpenses(String userId, String sortBy) {
        return switch (sortBy) {
            case "amount" -> repository.findAllByUserIdOrderByAmountDesc(userId);
            default       -> repository.findAllByUserIdOrderByTransactionDateDesc(userId);
        };
    }
}