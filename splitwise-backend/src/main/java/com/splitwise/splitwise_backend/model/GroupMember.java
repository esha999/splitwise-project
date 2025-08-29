package com.splitwise.splitwise_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "group_members")
@Data @NoArgsConstructor @AllArgsConstructor
public class GroupMember {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne @JoinColumn(name = "user_id")
    private User user;
}