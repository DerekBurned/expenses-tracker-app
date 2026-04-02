package org.example.expensestrack.services;

import org.example.expensestrack.Model.Settings;
import org.example.expensestrack.Model.SettingsDTO;
import org.example.expensestrack.Model.TransactionType;
import org.example.expensestrack.repository.SettingsRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class SettingsService {

    private final SettingsRepository repository;

    public SettingsService(SettingsRepository repository) {
        this.repository = repository;
    }

    public List<Settings> getCategories(String userId, TransactionType type) {
        return type != null
                ? repository.findAllByUserIdAndTransactionType(userId, type)
                : repository.findAllByUserId(userId);
    }

    /**
     * FIX: Previously the loop incremented `count` but never called
     * repository.save(), so categories sent from Android were silently dropped.
     * Now we build a batch list and call saveAll() once.
     */
    public int syncCategories(List<SettingsDTO> dtos) {
        List<Settings> toSave = new ArrayList<>();

        for (SettingsDTO dto : dtos) {
            if (!repository.existsByLocalId(dto.getLocalId())) {
                Settings settings = new Settings();
                settings.setLocalId(dto.getLocalId());
                settings.setName(dto.getName());
                settings.setTransactionType(dto.getTransactionType());
                settings.setUserId(dto.getUserId());
                toSave.add(settings);
            }
        }

        if (!toSave.isEmpty()) {
            repository.saveAll(toSave);
        }

        return toSave.size();
    }

    public boolean deleteCategory(String localId) {
        return repository.deleteByLocalId(localId);
    }
}