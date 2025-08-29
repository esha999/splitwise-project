package com.splitwise.splitwise_backend.controller;

import com.splitwise.splitwise_backend.dto.CreateExpenseRequest;
import com.splitwise.splitwise_backend.model.Expense;
import com.splitwise.splitwise_backend.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<Expense> createExpense(@RequestBody CreateExpenseRequest request) {
        return ResponseEntity.ok(expenseService.createExpense(request));
    }
}
