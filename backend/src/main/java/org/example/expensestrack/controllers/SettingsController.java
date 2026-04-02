package org.example.expensestrack.controllers;

import org.example.expensestrack.Model.Settings;
import org.example.expensestrack.Model.SettingsDTO;
import org.example.expensestrack.Model.TransactionType;
import org.example.expensestrack.services.SettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Fix: deleteCategory() previously relied on repository.deleteByLocalId()
 * returning a boolean. The repository fix changed that to void + @Modifying,
 * so the service (and controller) must use existsByLocalId() to determine
 * whether the record was present before deleting.
 */
@RestController
@RequestMapping("/api/categories")
public class SettingsController {

    private final SettingsService service;

    public SettingsController(SettingsService service) {
        this.service = service;
    }

    @GetMapping
    public List<Settings> getCategories(
            @RequestParam String userId,
            @RequestParam(required = false) TransactionType type
    ) {
        return service.getCategories(userId, type);
    }

    @PostMapping("/sync")
    public ResponseEntity<String> syncCategories(@RequestBody List<SettingsDTO> categories) {
        int count = service.syncCategories(categories);
        return ResponseEntity.ok("Synced " + count + " new categories.");
    }

    @DeleteMapping("/{localId}")
    public ResponseEntity<String> deleteCategory(@PathVariable String localId) {
        boolean existed = service.deleteCategory(localId);
        return existed
                ? ResponseEntity.ok("Deleted")
                : ResponseEntity.notFound().build();
    }
}