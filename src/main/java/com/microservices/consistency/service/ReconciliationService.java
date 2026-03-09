package com.microservices.consistency.service;

import com.microservices.consistency.entity.*;
import com.microservices.consistency.repository.DataSnapshotRepository;
import com.microservices.consistency.repository.ReconciliationRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReconciliationService {

    private final DataSnapshotRepository dataSnapshotRepository;
    private final ReconciliationRecordRepository reconciliationRecordRepository;

    @Scheduled(fixedDelayString = "${consistency.reconciliation.interval-ms:300000}")
    @Transactional
    public void performReconciliation() {
        log.info("Starting reconciliation process at {}", LocalDateTime.now());

        List<DataSnapshot> allSnapshots = dataSnapshotRepository.findAll();
        Map<String, List<DataSnapshot>> groupedSnapshots = allSnapshots.stream()
            .collect(Collectors.groupingBy(s -> s.getEntityType() + ":" + s.getEntityId()));

        int inconsistenciesFound = 0;
        for (Map.Entry<String, List<DataSnapshot>> entry : groupedSnapshots.entrySet()) {
            inconsistenciesFound += reconcileEntity(entry.getValue());
        }

        log.info("Reconciliation process completed. Found {} inconsistencies", inconsistenciesFound);
    }

    private int reconcileEntity(List<DataSnapshot> snapshots) {
        if (snapshots.size() < 2) return 0;

        DataSnapshot latest = snapshots.stream()
            .max((s1, s2) -> s1.getVersion().compareTo(s2.getVersion()))
            .orElse(null);

        if (latest == null) return 0;

        int inconsistencies = 0;

        for (DataSnapshot snapshot : snapshots) {
            if (!snapshot.getId().equals(latest.getId()) &&
                !snapshot.getDataPayload().equals(latest.getDataPayload())) {

                ReconciliationRecord record = new ReconciliationRecord();
                record.setEntityType(snapshot.getEntityType());
                record.setEntityId(snapshot.getEntityId());
                record.setSourceService(latest.getSourceService());
                record.setTargetService(snapshot.getSourceService());
                record.setStatus(ReconciliationStatus.DETECTED);
                record.setDiscrepancyDetails(String.format(
                    "Version mismatch: latest=%d, current=%d",
                    latest.getVersion(), snapshot.getVersion()));
                record.setDetectedAt(LocalDateTime.now());
                record.setAttemptCount(0);

                reconciliationRecordRepository.save(record);

                snapshot.setStatus(ConsistencyStatus.INCONSISTENT);
                dataSnapshotRepository.save(snapshot);

                log.warn("Inconsistency detected for entity {}:{} between {} and {}",
                    snapshot.getEntityType(), snapshot.getEntityId(),
                    latest.getSourceService(), snapshot.getSourceService());

                inconsistencies++;
            }
        }

        return inconsistencies;
    }

    @Transactional
    public void resolveInconsistency(Long recordId) {
        ReconciliationRecord record = reconciliationRecordRepository.findById(recordId)
            .orElseThrow(() -> new RuntimeException("Reconciliation record not found"));

        record.setStatus(ReconciliationStatus.RESOLVED);
        record.setResolvedAt(LocalDateTime.now());
        reconciliationRecordRepository.save(record);

        log.info("Inconsistency resolved for entity {}:{}", record.getEntityType(), record.getEntityId());
    }

    public List<ReconciliationRecord> getInconsistencies() {
        return reconciliationRecordRepository.findByStatus(ReconciliationStatus.DETECTED);
    }

    public List<ReconciliationRecord> getAllReconciliationRecords() {
        return reconciliationRecordRepository.findAll();
    }
}

