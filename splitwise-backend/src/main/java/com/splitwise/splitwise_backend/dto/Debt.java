package com.splitwise.splitwise_backend.dto;

import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Debt {
    private String from;     // debtor name
    private String to;       // creditor name
    private Double amount;   // amount owed
    private Long groupId;    // for reference (optional in UI)
}