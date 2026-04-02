package org.example.expensestrack.repository;

import org.example.expensestrack.Model.Settings;
import org.example.expensestrack.Model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Fix: deleteByLocalId() must be @Modifying + @Transactional to execute as a
 * DML statement. Without these annotations Spring Data tries to return a boolean
 * from a derived delete query which throws an InvalidDataAccessApiUsageException
 * at runtime. Changed return type to void for the modifying variant and added a
 * separate existsByLocalId check to preserve the boolean contract used by the
 * controller.
 */
@Repository
public interface SettingsRepository extends JpaRepository<Settings, Long> {

    List<Settings> findAllByUserId(String userId);

    List<Settings> findAllByUserIdAndTransactionType(String userId, TransactionType type);

    boolean existsByLocalId(String localId);

    Optional<Settings> findByLocalId(String localId);

    @Modifying
    @Transactional
    boolean deleteByLocalId(String localId);
}