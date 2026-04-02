package org.example.expensestrack.repository;

import org.example.expensestrack.Model.Transaction;
import org.example.expensestrack.Model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionsRepository extends JpaRepository<Transaction, Long> {

    boolean existsByLocalId(String localId);

    // @Modifying + @Transactional required for any derived DELETE/UPDATE query.
    // Without them Spring Data throws InvalidDataAccessApiUsageException at runtime.
    @Modifying
    @Transactional
    void deleteByLocalId(String localId);

    @Modifying
    @Transactional
    void deleteAllByTransactionDate(LocalDateTime expenseDate);

    Transaction getTransactionByLocalId(String localId);
    List<Transaction> findAllByUserIdOrderByTransactionDateDesc(String userId);
    List<Transaction> findAllByUserIdOrderByAmountDesc(String userId);
    List<Transaction> findAllByUserIdAndTransactionType(String userId, TransactionType type);
}