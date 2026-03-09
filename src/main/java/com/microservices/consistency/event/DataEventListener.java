package com.microservices.consistency.event;

import com.microservices.consistency.service.ConsistencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataEventListener {

    private final ConsistencyService consistencyService;

    @KafkaListener(topics = "data-events", groupId = "consistency-service-group")
    public void handleDataEvent(DataEvent event) {
        log.info("Received data event: entityType={}, entityId={}, sourceService={}, eventType={}",
            event.getEntityType(), event.getEntityId(), event.getSourceService(), event.getEventType());

        try {
            consistencyService.captureSnapshot(
                event.getEntityType(),
                event.getEntityId(),
                event.getSourceService(),
                event.getPayload(),
                event.getVersion()
            );
        } catch (Exception e) {
            log.error("Error processing data event: {}", e.getMessage(), e);
        }
    }
}

