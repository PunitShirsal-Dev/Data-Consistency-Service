package com.microservices.consistency.controller;

import com.microservices.consistency.dto.ConsistencyReport;
import com.microservices.consistency.entity.ConsistencyStatus;
import com.microservices.consistency.entity.DataSnapshot;
import com.microservices.consistency.entity.ReconciliationRecord;
import com.microservices.consistency.service.ConsistencyService;
import com.microservices.consistency.service.ReconciliationService;
import com.microservices.consistency.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consistency")
@RequiredArgsConstructor
public class ConsistencyController {

    private final ConsistencyService consistencyService;
    private final ReconciliationService reconciliationService;
    private final ReportingService reportingService;

    @PostMapping("/reconcile")
    public ResponseEntity<String> triggerReconciliation() {
        reconciliationService.performReconciliation();
        return ResponseEntity.ok("Reconciliation triggered successfully");
    }

    @GetMapping("/discrepancies")
    public ResponseEntity<List<ReconciliationRecord>> getDiscrepancies() {
        return ResponseEntity.ok(reconciliationService.getInconsistencies());
    }

    @GetMapping("/discrepancies/all")
    public ResponseEntity<List<ReconciliationRecord>> getAllDiscrepancies() {
        return ResponseEntity.ok(reconciliationService.getAllReconciliationRecords());
    }

    @PostMapping("/discrepancies/{id}/resolve")
    public ResponseEntity<String> resolveDiscrepancy(@PathVariable Long id) {
        reconciliationService.resolveInconsistency(id);
        return ResponseEntity.ok("Discrepancy resolved successfully");
    }

    @GetMapping("/snapshots")
    public ResponseEntity<List<DataSnapshot>> getAllSnapshots() {
        return ResponseEntity.ok(consistencyService.getAllSnapshots());
    }

    @GetMapping("/snapshots/{entityType}/{entityId}")
    public ResponseEntity<List<DataSnapshot>> getSnapshotsByEntity(
            @PathVariable String entityType,
            @PathVariable String entityId) {
        return ResponseEntity.ok(consistencyService.getSnapshotsByEntity(entityType, entityId));
    }

    @GetMapping("/snapshots/status/{status}")
    public ResponseEntity<List<DataSnapshot>> getSnapshotsByStatus(
            @PathVariable ConsistencyStatus status) {
        return ResponseEntity.ok(consistencyService.getSnapshotsByStatus(status));
    }

    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("Data Consistency Service is running");
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/report")
    public ResponseEntity<ConsistencyReport> getConsistencyReport() {
        return ResponseEntity.ok(reportingService.generateReport());
    }
}

