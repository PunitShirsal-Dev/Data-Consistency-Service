package com.microservices.consistency.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "reconciliation_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String entityType;

    @Column(nullable = false)
    private String entityId;

    private String sourceService;

    private String targetService;

    @Enumerated(EnumType.STRING)
    private ReconciliationStatus status;

    @Column(columnDefinition = "TEXT")
    private String discrepancyDetails;

    private LocalDateTime detectedAt;

    private LocalDateTime resolvedAt;

    private Integer attemptCount;
}

