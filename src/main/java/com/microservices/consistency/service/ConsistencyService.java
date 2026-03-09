package com.microservices.consistency.service;

import com.microservices.consistency.entity.ConsistencyStatus;
import com.microservices.consistency.entity.DataSnapshot;
import com.microservices.consistency.repository.DataSnapshotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsistencyService {

    private final DataSnapshotRepository dataSnapshotRepository;

    @Transactional
    public void captureSnapshot(String entityType, String entityId,
                                String sourceService, String dataPayload, Long version) {
        log.info("Capturing snapshot for entity {}:{} from service {}", entityType, entityId, sourceService);

        DataSnapshot snapshot = dataSnapshotRepository
            .findByEntityTypeAndEntityIdAndSourceService(entityType, entityId, sourceService)
            .orElse(new DataSnapshot());

        snapshot.setEntityType(entityType);
        snapshot.setEntityId(entityId);
        snapshot.setSourceService(sourceService);
        snapshot.setDataPayload(dataPayload);
        snapshot.setVersion(version);
        snapshot.setTimestamp(LocalDateTime.now());
        snapshot.setStatus(ConsistencyStatus.CONSISTENT);

        dataSnapshotRepository.save(snapshot);
        log.info("Snapshot captured successfully for {}:{} from {}", entityType, entityId, sourceService);
    }

    public List<DataSnapshot> getSnapshotsByEntity(String entityType, String entityId) {
        return dataSnapshotRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    public List<DataSnapshot> getSnapshotsByStatus(ConsistencyStatus status) {
        return dataSnapshotRepository.findByStatus(status);
    }

    public List<DataSnapshot> getAllSnapshots() {
        return dataSnapshotRepository.findAll();
    }
}

