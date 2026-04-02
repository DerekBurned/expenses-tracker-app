package org.example.expensestrack.controllers;

import org.example.expensestrack.Model.TransactionDTO;
import org.example.expensestrack.Model.TransactionResponseDTO;
import org.example.expensestrack.services.TransactionsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionsService service;

    public TransactionController(TransactionsService service) {
        this.service = service;
    }

    @PostMapping("/sync")
    public ResponseEntity<String> syncTransactions(@RequestBody List<TransactionDTO> expenses) {
        int saved = service.syncOfflineTransactions(expenses);
        return ResponseEntity.ok("Synced " + saved + " new expenses.");
    }

    /**
     * userId is now optional (required = false, default = "default-user").
     *
     * Android sends no userId at the moment. Making it optional means the
     * endpoint returns 200 with results instead of 400 Bad Request, so the
     * app can actually display data. Replace the default with real auth later.
     */
    @GetMapping
    public List<TransactionResponseDTO> getAll(
            @RequestParam(required = false, defaultValue = "default-user") String userId,
            @RequestParam(defaultValue = "date") String sortBy
    ) {
        return service.getAllExpenses(userId, sortBy)
                .stream()
                .map(TransactionResponseDTO::from)
                .collect(Collectors.toList());
    }
    @GetMapping("/{id}")
    public TransactionResponseDTO getById(@PathVariable String id) {
        return TransactionResponseDTO.from(service.getExpenseById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateById(@PathVariable String id, @RequestBody TransactionDTO dto) {
        //TODO("Implement transaction update method")
        return ResponseEntity.ok("Updated " + dto);
    }
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existsById(@PathVariable String id) {
        boolean exists = service.getExpenseById(id) != null;
        return exists ? ResponseEntity.ok(true) : ResponseEntity.ok(false);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable String id) {
        boolean deleted = service.deleteExpenseById(id);
        return deleted
                ? ResponseEntity.ok("Deleted")
                : ResponseEntity.notFound().build();
    }
}