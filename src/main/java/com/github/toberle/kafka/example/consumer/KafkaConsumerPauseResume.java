package com.github.toberle.kafka.example.consumer;

import com.github.toberle.kafka.example.manager.KafkaPauseResumeManager;
import com.github.toberle.kafka.example.service.AsyncLongPauseResumeProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * The Kafka listener with pause/resume for long processing ("max.poll.interval.ms" and Heartbeat).
 * The processing needs to be done in "separate thread" than consumer/listener.
 * After processing in separate thread: ack message and resume consumer via "manager".
 */
@Component
@ConditionalOnProperty(value = "kafka-consumer-type", havingValue = "KAFKA_LISTENER_PAUSE_RESUME")
public class KafkaConsumerPauseResume {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerPauseResume.class);

    private final KafkaPauseResumeManager kafkaPauseResumeManager;
    private final AsyncLongPauseResumeProcessingService asyncLongPauseResumeProcessingService;

    public KafkaConsumerPauseResume(KafkaPauseResumeManager kafkaPauseResumeManager, AsyncLongPauseResumeProcessingService asyncLongPauseResumeProcessingService) {
        this.kafkaPauseResumeManager = kafkaPauseResumeManager;
        this.asyncLongPauseResumeProcessingService = asyncLongPauseResumeProcessingService;
    }

    @KafkaListener(
            topics = "test-topic",
            id = "kafka-listener-pause-resume" // same ID as in "KafkaPauseResumeManager"
    )
    public void listener(@Payload String payload, Acknowledgment ack) {
        log.info("Consuming message in listener (pause-resume): {}", payload);

        log.info("Pausing consumer for '{}'", payload);
        kafkaPauseResumeManager.pause();
        log.info("Consumer was paused for '{}'", payload);

        // Consumer properties: max.poll.records=1 and ack-mode=MANUAL
        // Process in separate thread - acknowledge() message and resume() consumer at the end of processing [IMPORTANT]
        asyncLongPauseResumeProcessingService.process(payload, ack);

        log.info("End of listener method (message: {})", payload);
    }
}
