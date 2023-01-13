package com.github.toberle.kafka.example.consumer;

import com.github.toberle.kafka.example.dto.TestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "kafka-consumer-type", havingValue = "KAFKA_LISTENER_JSON")
public class KafkaJsonConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaJsonConsumer.class);

    @KafkaListener(topics = "json-topic")
    public void listen(TestDto testDto) {
        log.info("{}", testDto);
    }


}
