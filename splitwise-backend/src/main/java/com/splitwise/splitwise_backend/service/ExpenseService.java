package com.splitwise.splitwise_backend.service;

import com.splitwise.splitwise_backend.dto.CreateExpenseRequest;
import com.splitwise.splitwise_backend.model.*;
import com.splitwise.splitwise_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepo;
    private final ExpenseShareRepository shareRepo;
    private final GroupRepository groupRepo;
    private final GroupMemberRepository memberRepo;
    private final UserRepository userRepo;

    @Transactional
    public Expense createExpense(CreateExpenseRequest req) {
        if (req.getGroupId() == null) throw new RuntimeException("groupId is required");
        if (req.getPayerId() == null) throw new RuntimeException("payerId is required");
        if (req.getAmount() == null || req.getAmount() <= 0) throw new RuntimeException("amount must be > 0");
        if (req.getSplitType() == null) throw new RuntimeException("splitType is required");

        Group group = groupRepo.findById(req.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found: " + req.getGroupId()));

        User payer = userRepo.findById(req.getPayerId())
                .orElseThrow(() -> new RuntimeException("Payer not found: " + req.getPayerId()));

        // create expense row
        Expense expense = Expense.builder()
                .description(req.getDescription())
                .amount(req.getAmount())
                .payer(payer)
                .group(group)
                .splitType(req.getSplitType())
                .build();

        expense = expenseRepo.save(expense);

        // group members
        List<GroupMember> members = memberRepo.findByGroup_Id(group.getId());
        if (members.isEmpty()) throw new RuntimeException("Group has no members");

        // do splits
        Map<Long, Double> splits = Optional.ofNullable(req.getSplits()).orElseGet(HashMap::new);
        int n = members.size();
        BigDecimal total = bd(req.getAmount());

        switch (req.getSplitType()) {
            case EQUAL -> {
                BigDecimal share = total.divide(bd(n), 2, RoundingMode.HALF_UP);
                BigDecimal assigned = BigDecimal.ZERO;
                for (int i = 0; i < n; i++) {
                    User u = members.get(i).getUser();
                    BigDecimal amt = (i == n - 1) ? total.subtract(assigned) : share;
                    assigned = assigned.add(amt);
                    shareRepo.save(ExpenseShare.builder()
                            .expense(expense)
                            .user(u)
                            .amount(amt.doubleValue())
                            .build());
                }
            }
            case EXACT -> {
                if (splits.size() != n)
                    throw new RuntimeException("Provide exact amount for each member");

                BigDecimal sum = BigDecimal.ZERO;
                for (GroupMember gm : members) {
                    Long uid = gm.getUser().getId();
                    Double val = splits.get(uid);
                    if (val == null) throw new RuntimeException("Missing exact share for user " + uid);
                    BigDecimal amt = bd(val).setScale(2, RoundingMode.HALF_UP);
                    sum = sum.add(amt);
                    shareRepo.save(ExpenseShare.builder()
                            .expense(expense)
                            .user(gm.getUser())
                            .amount(amt.doubleValue())
                            .build());
                }
                if (sum.compareTo(total.setScale(2, RoundingMode.HALF_UP)) != 0)
                    throw new RuntimeException("EXACT shares must sum to total");
            }
            case PERCENT -> {
                if (splits.size() != n)
                    throw new RuntimeException("Provide percent for each member");

                BigDecimal pctSum = BigDecimal.ZERO;
                BigDecimal assigned = BigDecimal.ZERO;

                for (int i = 0; i < n; i++) {
                    GroupMember gm = members.get(i);
                    Long uid = gm.getUser().getId();
                    Double pct = splits.get(uid);
                    if (pct == null) throw new RuntimeException("Missing percent for user " + uid);
                    pctSum = pctSum.add(bd(pct));
                    BigDecimal amt;
                    if (i == n - 1) {
                        amt = total.subtract(assigned);
                    } else {
                        amt = total.multiply(bd(pct).divide(bd(100), 6, RoundingMode.HALF_UP))
                                .setScale(2, RoundingMode.HALF_UP);
                        assigned = assigned.add(amt);
                    }
                    shareRepo.save(ExpenseShare.builder()
                            .expense(expense)
                            .user(gm.getUser())
                            .amount(amt.doubleValue())
                            .build());
                }
                if (pctSum.setScale(2, RoundingMode.HALF_UP).compareTo(bd(100)) != 0)
                    throw new RuntimeException("Percent must total 100");
            }
        }

        return expense;
    }

    private static BigDecimal bd(double d) {
        return BigDecimal.valueOf(d);
    }
}
