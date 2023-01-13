package com.github.toberle.kafka.example.consumer;

import com.github.toberle.kafka.example.dto.MessageDto;
import com.github.toberle.kafka.example.dto.ValueDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "kafka-consumer-type", havingValue = "KAFKA_LISTENER_JSON_HANDLER")
@KafkaListener(topics = "json-handler-topic")
public class KafkaJsonHandlerConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaJsonHandlerConsumer.class);

    @KafkaHandler
    public void listen(MessageDto messageDto) {
        log.info("MessageDto: {}", messageDto.getMessage());
    }

    @KafkaHandler
    public void listen(ValueDto messageDto) {
        log.info("ValueDto: {}", messageDto.getValue());
    }
}
