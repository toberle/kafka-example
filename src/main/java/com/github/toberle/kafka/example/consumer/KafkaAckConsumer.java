package com.github.toberle.kafka.example.consumer;

import com.github.toberle.kafka.example.service.ProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "kafka-consumer-type", havingValue = "KAFKA_LISTENER_ACK")
public class KafkaAckConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaAckConsumer.class);

    private final ProcessingService processingService;

    public KafkaAckConsumer(ProcessingService processingService) {
        this.processingService = processingService;
    }

    @KafkaListener(topics = "test-topic")
    public void listener(@Payload String payload, Acknowledgment acknowledgment) {
        log.info("Consuming message in listener (with ACK): {}", payload);
        processingService.process(payload);
        acknowledgment.acknowledge(); // BREAKPOINT (CommitFailedException) - timeout
        log.info("Kafka ACK has been done for: {}", payload);
    }
}
