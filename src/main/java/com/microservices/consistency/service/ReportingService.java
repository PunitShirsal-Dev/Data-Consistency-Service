package com.microservices.consistency.service;

import com.microservices.consistency.dto.ConsistencyReport;
import com.microservices.consistency.entity.ConsistencyStatus;
import com.microservices.consistency.entity.ReconciliationStatus;
import com.microservices.consistency.repository.DataSnapshotRepository;
import com.microservices.consistency.repository.ReconciliationRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportingService {

    private final DataSnapshotRepository dataSnapshotRepository;
    private final ReconciliationRecordRepository reconciliationRecordRepository;

    public ConsistencyReport generateReport() {
        ConsistencyReport report = new ConsistencyReport();

        long totalSnapshots = dataSnapshotRepository.count();
        long consistentSnapshots = dataSnapshotRepository.findByStatus(ConsistencyStatus.CONSISTENT).size();
        long inconsistentSnapshots = dataSnapshotRepository.findByStatus(ConsistencyStatus.INCONSISTENT).size();
        long pendingReconciliation = dataSnapshotRepository.findByStatus(ConsistencyStatus.PENDING_RECONCILIATION).size();

        long totalDiscrepancies = reconciliationRecordRepository.count();
        long resolvedDiscrepancies = reconciliationRecordRepository.findByStatus(ReconciliationStatus.RESOLVED).size();
        long unresolvedDiscrepancies = totalDiscrepancies - resolvedDiscrepancies;

        double consistencyRate = totalSnapshots > 0
            ? (double) consistentSnapshots / totalSnapshots * 100
            : 100.0;

        report.setTotalSnapshots(totalSnapshots);
        report.setConsistentSnapshots(consistentSnapshots);
        report.setInconsistentSnapshots(inconsistentSnapshots);
        report.setPendingReconciliation(pendingReconciliation);
        report.setTotalDiscrepancies(totalDiscrepancies);
        report.setResolvedDiscrepancies(resolvedDiscrepancies);
        report.setUnresolvedDiscrepancies(unresolvedDiscrepancies);
        report.setConsistencyRate(consistencyRate);

        log.info("Generated consistency report: consistencyRate={}%, totalSnapshots={}, inconsistentSnapshots={}",
            String.format("%.2f", consistencyRate), totalSnapshots, inconsistentSnapshots);

        return report;
    }
}

