package com.microservices.consistency.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataEvent {
    private String entityType;
    private String entityId;
    private String sourceService;
    private String payload;
    private Long version;
    private String eventType; // CREATE, UPDATE, DELETE
}

