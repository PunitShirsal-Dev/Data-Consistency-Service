package com.microservices.consistency.repository;

import com.microservices.consistency.entity.ReconciliationRecord;
import com.microservices.consistency.entity.ReconciliationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReconciliationRecordRepository extends JpaRepository<ReconciliationRecord, Long> {

    List<ReconciliationRecord> findByStatus(ReconciliationStatus status);

    List<ReconciliationRecord> findByEntityTypeAndEntityId(String entityType, String entityId);

    List<ReconciliationRecord> findBySourceServiceAndTargetService(String sourceService, String targetService);
}

