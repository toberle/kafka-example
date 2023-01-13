package com.github.toberle.kafka.example.configuration;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.List;

@Configuration
public class ReactiveKafkaConsumerConfiguration {

    @Bean
    public ReceiverOptions<String,String> kafkaConsumerReceiverOptions(KafkaProperties properties) {
        ReceiverOptions<String, String> receiverOptions = ReceiverOptions.create(properties.buildConsumerProperties());
        return receiverOptions.subscription(List.of("test-topic"));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String,String> reactiveKafkaConsumerTemplate(
            ReceiverOptions<String, String> receiverOptions) {
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }
}
