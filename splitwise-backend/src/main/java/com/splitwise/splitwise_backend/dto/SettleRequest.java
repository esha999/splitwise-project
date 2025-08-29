// src/main/java/com/splitwise/splitwise_backend/dto/SettleRequest.java
package com.splitwise.splitwise_backend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class SettleRequest {
    private Long groupId;

    // accept either "fromUserId" or "payerId"
    @JsonAlias({"fromUserId", "payerId"})
    private Long fromUserId;

    // accept either "toUserId" or "receiverId"
    @JsonAlias({"toUserId", "receiverId"})
    private Long toUserId;

    private Double amount; // partial or full
}
