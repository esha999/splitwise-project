package com.splitwise.splitwise_backend.dto;

import com.splitwise.splitwise_backend.model.SplitType;
import lombok.Data;

import java.util.Map;

@Data
public class CreateExpenseRequest {
    private Long groupId;
    private Long payerId;
    private Double amount;
    private String description;
    private SplitType splitType;         // EQUAL / EXACT / PERCENT
    private Map<Long, Double> splits;    // for EXACT: userId->amount, for PERCENT: userId->percent
}
