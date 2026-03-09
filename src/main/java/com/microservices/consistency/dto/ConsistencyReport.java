package com.microservices.consistency.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsistencyReport {
    private long totalSnapshots;
    private long consistentSnapshots;
    private long inconsistentSnapshots;
    private long pendingReconciliation;
    private long totalDiscrepancies;
    private long resolvedDiscrepancies;
    private long unresolvedDiscrepancies;
    private double consistencyRate;
}

