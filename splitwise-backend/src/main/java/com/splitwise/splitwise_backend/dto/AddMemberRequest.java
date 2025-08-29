package com.splitwise.splitwise_backend.dto;

import lombok.Data;
@Data public class AddMemberRequest {
    private Long groupId;
    private Long userId;
}
