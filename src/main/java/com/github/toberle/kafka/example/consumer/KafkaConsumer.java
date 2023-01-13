package com.github.toberle.kafka.example.consumer;

import com.github.toberle.kafka.example.service.ProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "kafka-consumer-type", havingValue = "KAFKA_LISTENER")
public class KafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    private final ProcessingService processingService;

    public KafkaConsumer(ProcessingService processingService) {
        this.processingService = processingService;
    }

    @KafkaListener(topics = "test-topic")
    public void listener(@Payload String payload) {
        log.info("Consuming message in listener: {}", payload);
        processingService.process(payload); // BREAKPOINT (CommitFailedException) - timeout
    }
}
