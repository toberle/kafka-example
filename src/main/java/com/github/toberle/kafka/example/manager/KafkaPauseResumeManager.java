package com.github.toberle.kafka.example.manager;

import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class KafkaPauseResumeManager {

    private static final String LISTENER_CONTAINER_ID = "kafka-listener-pause-resume";
    private final KafkaListenerEndpointRegistry registry;

    public KafkaPauseResumeManager(KafkaListenerEndpointRegistry registry) {
        this.registry = registry;
    }

    public void pause() {
        MessageListenerContainer listenerContainer = getListenerContainer().orElseThrow(
                () -> new IllegalStateException("Pause failed for listener container ID: " + LISTENER_CONTAINER_ID));
        listenerContainer.pause();
    }

    public void resume() {
        MessageListenerContainer listenerContainer = getListenerContainer().orElseThrow(
                () -> new IllegalStateException("Resume failed for listener container ID: " + LISTENER_CONTAINER_ID));
        listenerContainer.resume();
    }

    private Optional<MessageListenerContainer> getListenerContainer() {
        return Optional.ofNullable(registry.getListenerContainer(LISTENER_CONTAINER_ID));
    }
}
