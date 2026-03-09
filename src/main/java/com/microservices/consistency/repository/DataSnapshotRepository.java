package com.microservices.consistency.repository;

import com.microservices.consistency.entity.DataSnapshot;
import com.microservices.consistency.entity.ConsistencyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataSnapshotRepository extends JpaRepository<DataSnapshot, Long> {

    Optional<DataSnapshot> findByEntityTypeAndEntityIdAndSourceService(
        String entityType, String entityId, String sourceService);

    List<DataSnapshot> findByEntityTypeAndEntityId(String entityType, String entityId);

    List<DataSnapshot> findByStatus(ConsistencyStatus status);

    List<DataSnapshot> findBySourceService(String sourceService);
}

