package com.microservices.consistency.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "data_snapshots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String entityType;

    @Column(nullable = false)
    private String entityId;

    @Column(nullable = false)
    private String sourceService;

    @Column(columnDefinition = "TEXT")
    private String dataPayload;

    private Long version;

    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    private ConsistencyStatus status;
}

