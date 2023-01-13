package com.github.toberle.kafka.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
public class ReactiveProcessingServiceImpl implements ReactiveProcessingService {

    private static final Logger log = LoggerFactory.getLogger(ReactiveProcessingServiceImpl.class);

    @Override
    public Mono<Void> process(String value) {
        log.info("Consuming message: {}", value);
        if (value.startsWith("ERROR")) {
            return simulateErrorDuringProcessing(value);
        } else if (value.startsWith("ERROR_RETRY")) {
            return simulateErrorWithRetryDuringProcessing(value);
        } else if (value.startsWith("LONG_PROCESSING")) {
            return simulateLongProcessing(value);
        } else {
            return simulateProcessing(value);
        }
    }

    private Mono<Void> simulateProcessing(String value) {
        log.info("Consumer processing started: {}", value);
        return Mono.delay(Duration.ofSeconds(3))
                .flatMap(aLong -> {
                    log.info("Consumer processing successfully finished: {} with delay {}", value, aLong);
                    return Mono.empty();
                }).then();
    }

    private Mono<Void> simulateErrorDuringProcessing(String value) {
        log.info("Processing will fail with error (exception)...");
        return Mono.error(() -> new RuntimeException("Processing failed with exception for: " + value));
    }

    private Mono<Void> simulateErrorWithRetryDuringProcessing(String value) {
        log.info("Processing will fail with error after retry (exception)...");
        return Mono.error(() -> new RuntimeException("Processing failed with exception (retry) for: " + value))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)))
                .then();
    }

    private Mono<Void> simulateLongProcessing(String value) {
        log.info("Consumer long processing started: {}", value);
        return Mono.delay(Duration.ofSeconds(10))
                .flatMap(aLong -> {
                    log.info("Consumer long processing successfully finished: {} with delay {}", value, aLong);
                    return Mono.empty();
                });
    }
}
