package com.splitwise.splitwise_backend.service;

import com.splitwise.splitwise_backend.dto.Debt;
import com.splitwise.splitwise_backend.model.*;
import com.splitwise.splitwise_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final GroupMemberRepository memberRepo;
    private final ExpenseRepository expenseRepo;
    private final ExpenseShareRepository shareRepo;
    private final SettlementRepository settlementRepo;

    // ✅ needed for recordSettlement
    private final GroupRepository groupRepo;
    private final UserRepository userRepo;

    private BigDecimal bd(Double value) {
        return BigDecimal.valueOf(value != null ? value : 0.0);
    }

    /** Map<username, balance> */
    public Map<String, BigDecimal> calculateNetBalances(Long groupId) {
        Map<Long, BigDecimal> net = new HashMap<>();
        Map<String, BigDecimal> result = new HashMap<>();

        for (GroupMember gm : memberRepo.findByGroup_Id(groupId)) {
            net.putIfAbsent(gm.getUser().getId(), BigDecimal.ZERO);
        }
        for (Expense e : expenseRepo.findByGroup_Id(groupId)) {
            net.merge(e.getPayer().getId(), bd(e.getAmount()), BigDecimal::add);
        }
        for (ExpenseShare s : shareRepo.findByExpense_Group_Id(groupId)) {
            net.merge(s.getUser().getId(), bd(s.getAmount()).negate(), BigDecimal::add);
        }
        for (SettlementRecord sr : settlementRepo.findByGroup_Id(groupId)) {
            net.merge(sr.getFromUser().getId(), bd(sr.getAmount()), BigDecimal::add);
            net.merge(sr.getToUser().getId(), bd(sr.getAmount()).negate(), BigDecimal::add);
        }
        net.replaceAll((k, v) -> v.setScale(2, RoundingMode.HALF_UP));

        for (GroupMember gm : memberRepo.findByGroup_Id(groupId)) {
            Long userId = gm.getUser().getId();
            String username = gm.getUser().getName();
            result.put(username, net.getOrDefault(userId, BigDecimal.ZERO));
        }
        return result;
    }

    /** Optional: string suggestions */
    public List<String> settleUp(Long groupId) {
        Map<String, BigDecimal> balances = calculateNetBalances(groupId);

        PriorityQueue<Map.Entry<String, BigDecimal>> creditors =
                new PriorityQueue<>((a, b) -> b.getValue().compareTo(a.getValue()));
        PriorityQueue<Map.Entry<String, BigDecimal>> debtors =
                new PriorityQueue<>((a, b) -> a.getValue().compareTo(b.getValue()));

        for (Map.Entry<String, BigDecimal> e : balances.entrySet()) {
            if (e.getValue().compareTo(BigDecimal.ZERO) > 0) creditors.add(e);
            else if (e.getValue().compareTo(BigDecimal.ZERO) < 0) debtors.add(e);
        }

        List<String> txns = new ArrayList<>();
        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            var cred = creditors.poll();
            var debt = debtors.poll();
            BigDecimal pay = cred.getValue().min(debt.getValue().negate()).setScale(2, RoundingMode.HALF_UP);
            txns.add(debt.getKey() + " pays " + pay + " to " + cred.getKey());
            cred.setValue(cred.getValue().subtract(pay));
            debt.setValue(debt.getValue().add(pay));
            if (cred.getValue().compareTo(BigDecimal.ZERO) > 0) creditors.add(cred);
            if (debt.getValue().compareTo(BigDecimal.ZERO) < 0) debtors.add(debt);
        }
        return txns;
    }

    /** Structured suggestions */
    public List<Debt> suggestedDebts(Long groupId) {
        Map<Long, BigDecimal> netById = new HashMap<>();

        for (GroupMember gm : memberRepo.findByGroup_Id(groupId)) {
            netById.putIfAbsent(gm.getUser().getId(), BigDecimal.ZERO);
        }
        for (Expense e : expenseRepo.findByGroup_Id(groupId)) {
            netById.merge(e.getPayer().getId(), bd(e.getAmount()), BigDecimal::add);
        }
        for (ExpenseShare s : shareRepo.findByExpense_Group_Id(groupId)) {
            netById.merge(s.getUser().getId(), bd(s.getAmount()).negate(), BigDecimal::add);
        }
        for (SettlementRecord sr : settlementRepo.findByGroup_Id(groupId)) {
            netById.merge(sr.getFromUser().getId(), bd(sr.getAmount()), BigDecimal::add);
            netById.merge(sr.getToUser().getId(), bd(sr.getAmount()).negate(), BigDecimal::add);
        }
        netById.replaceAll((k, v) -> v.setScale(2, RoundingMode.HALF_UP));

        Map<Long, String> idToName = new HashMap<>();
        for (GroupMember gm : memberRepo.findByGroup_Id(groupId)) {
            idToName.put(gm.getUser().getId(), gm.getUser().getName());
        }

        PriorityQueue<Map.Entry<Long, BigDecimal>> creditors =
                new PriorityQueue<>((a, b) -> b.getValue().compareTo(a.getValue()));
        PriorityQueue<Map.Entry<Long, BigDecimal>> debtors =
                new PriorityQueue<>((a, b) -> a.getValue().compareTo(b.getValue()));

        for (Map.Entry<Long, BigDecimal> e : netById.entrySet()) {
            if (e.getValue().compareTo(BigDecimal.ZERO) > 0) creditors.add(new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue()));
            else if (e.getValue().compareTo(BigDecimal.ZERO) < 0) debtors.add(new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue()));
        }

        List<Debt> out = new ArrayList<>();
        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            var cred = creditors.poll();
            var debt = debtors.poll();

            BigDecimal pay = cred.getValue().min(debt.getValue().negate()).setScale(2, RoundingMode.HALF_UP);
            out.add(Debt.builder()
                    .from(idToName.getOrDefault(debt.getKey(), "User " + debt.getKey()))
                    .to(idToName.getOrDefault(cred.getKey(), "User " + cred.getKey()))
                    .amount(pay.doubleValue())
                    .groupId(groupId)
                    .build());

            BigDecimal credLeft = cred.getValue().subtract(pay);
            BigDecimal debtLeft = debt.getValue().add(pay);
            if (credLeft.compareTo(BigDecimal.ZERO) > 0) creditors.add(new AbstractMap.SimpleEntry<>(cred.getKey(), credLeft));
            if (debtLeft.compareTo(BigDecimal.ZERO) < 0) debtors.add(new AbstractMap.SimpleEntry<>(debt.getKey(), debtLeft));
        }
        return out;
    }

    /** ✅ Save a settlement payment (person -> person) */
    public void recordSettlement(Long groupId, Long fromUserId, Long toUserId, Double amount) {
        if (groupId == null) throw new RuntimeException("groupId is required");
        if (fromUserId == null) throw new RuntimeException("fromUserId/payerId is required");
        if (toUserId == null) throw new RuntimeException("toUserId/receiverId is required");
        if (Objects.equals(fromUserId, toUserId)) throw new RuntimeException("payer and receiver cannot be same");
        if (amount == null || amount <= 0) throw new RuntimeException("amount must be > 0");

        Group group = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found: " + groupId));
        User from = userRepo.findById(fromUserId)
                .orElseThrow(() -> new RuntimeException("Payer not found: " + fromUserId));
        User to = userRepo.findById(toUserId)
                .orElseThrow(() -> new RuntimeException("Receiver not found: " + toUserId));

        // (optional) check membership
        boolean fromIsMember = memberRepo.findByGroup_Id(groupId).stream()
                .anyMatch(gm -> gm.getUser().getId().equals(fromUserId));
        boolean toIsMember = memberRepo.findByGroup_Id(groupId).stream()
                .anyMatch(gm -> gm.getUser().getId().equals(toUserId));
        if (!fromIsMember || !toIsMember) {
            throw new RuntimeException("Both users must be members of the group");
        }

        SettlementRecord rec = new SettlementRecord();
        rec.setGroup(group);          // writes to group_ref_id ✅
        rec.setFromUser(from);        // writes to from_user_id ✅
        rec.setToUser(to);            // writes to to_user_id   ✅
        rec.setAmount(amount);
        rec.setCreatedAt(Instant.now());

        settlementRepo.save(rec);
    }
}
