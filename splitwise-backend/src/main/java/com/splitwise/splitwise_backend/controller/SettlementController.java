package com.splitwise.splitwise_backend.controller;

import com.splitwise.splitwise_backend.dto.Debt;
import com.splitwise.splitwise_backend.dto.SettleRequest;
import com.splitwise.splitwise_backend.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    // Balances: username -> net amount
    @GetMapping("/settlements/balances/{groupId}")
    public Map<String, BigDecimal> getNetBalances(@PathVariable Long groupId) {
        return settlementService.calculateNetBalances(groupId);
    }

    // Alias used by your Balance page
    @GetMapping("/groups/{groupId}/balances")
    public Map<String, BigDecimal> getGroupBalances(@PathVariable Long groupId) {
        return settlementService.calculateNetBalances(groupId);
    }

    // ✅ NEW: Suggested person-to-person payments (readable)
    @GetMapping("/groups/{groupId}/debts")
    public List<Debt> getSuggestedDebts(@PathVariable Long groupId) {
        return settlementService.suggestedDebts(groupId);
    }

    // ✅ NEW — person-to-person settlement
    @PostMapping("/settle")
    public ResponseEntity<Void> settle(@RequestBody SettleRequest req) {
        settlementService.recordSettlement(
                req.getGroupId(),
                req.getFromUserId(),
                req.getToUserId(),
                req.getAmount()
        );
        return ResponseEntity.ok().build();
    }


}
