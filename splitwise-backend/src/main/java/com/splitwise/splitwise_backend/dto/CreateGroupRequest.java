package com.splitwise.splitwise_backend.dto;

import lombok.Data;
import java.util.Set;
@Data public class CreateGroupRequest {
    private String name;
    private Set<Long> memberIds; // optional
}

