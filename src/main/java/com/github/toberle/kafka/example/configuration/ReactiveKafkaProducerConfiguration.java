package com.github.toberle.kafka.example.configuration;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.sender.SenderOptions;

import java.util.Map;

@Configuration
public class ReactiveKafkaProducerConfiguration {

    @Bean
    public ReactiveKafkaProducerTemplate<String,String> reactiveKafkaProducerTemplate(KafkaProperties properties) {
        Map<String, Object> producerProperties = properties.buildProducerProperties();
        return new ReactiveKafkaProducerTemplate<>(SenderOptions.create(producerProperties));
    }
}
