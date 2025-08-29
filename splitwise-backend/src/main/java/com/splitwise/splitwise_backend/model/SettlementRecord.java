package com.splitwise.splitwise_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "settlement_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettlementRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

    // if your DB column is NOT NULL, either keep this default OR have a DB default
    private Instant createdAt = Instant.now();

    @ManyToOne
    @JoinColumn(name = "from_user_id", nullable = false)   // ✅ matches table
    private User fromUser;

    @ManyToOne
    @JoinColumn(name = "to_user_id", nullable = false)     // ✅ matches table
    private User toUser;

    @ManyToOne
    @JoinColumn(name = "group_ref_id", nullable = false)   // ✅ matches table
    private Group group;
}
