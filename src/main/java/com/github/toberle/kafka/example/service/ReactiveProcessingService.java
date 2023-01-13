package com.github.toberle.kafka.example.service;

import reactor.core.publisher.Mono;

public interface ReactiveProcessingService {

    Mono<Void> process(String value);
}
