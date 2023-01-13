package com.github.toberle.kafka.example.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
public class ReactiveKafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(ReactiveKafkaProducer.class);

    private static final String TOPIC = "test-topic";

    private final ReactiveKafkaProducerTemplate<String, String> producerTemplate;

    public ReactiveKafkaProducer(ReactiveKafkaProducerTemplate<String, String> producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @PostMapping(path = "/reactive/send", produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<ResponseEntity<String>> send() {
        return Mono.just(LocalDateTime.now().toString())
                .doOnNext(s -> log.info("Sending message '{}' to topic '{}'", s, TOPIC))
                .flatMap(dateAsString -> producerTemplate.send(TOPIC, dateAsString))
                .flatMap(voidSenderResult -> {
                    if (voidSenderResult.exception() != null) {
                        return Mono.just(ResponseEntity.internalServerError().body("ERROR\n"));
                    }
                    return Mono.just(ResponseEntity.ok("OK\n"));
                });
    }
}
