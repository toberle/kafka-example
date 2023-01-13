package com.github.toberle.kafka.example.consumer;

import com.github.toberle.kafka.example.service.ReactiveProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

@Component
@ConditionalOnProperty(value = "kafka-consumer-type", havingValue = "REACTIVE_DELAY_UNTIL")
public class ReactiveKafkaConsumerDelayUntil implements InitializingBean, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(ReactiveKafkaConsumerDelayUntil.class);

    private final ReactiveProcessingService reactiveProcessingService;
    private final ReactiveKafkaConsumerTemplate<String, String> consumerTemplate;
    private Disposable consumer;

    public ReactiveKafkaConsumerDelayUntil(ReactiveProcessingService reactiveProcessingService,
                                           ReactiveKafkaConsumerTemplate<String, String> consumerTemplate) {
        this.reactiveProcessingService = reactiveProcessingService;
        this.consumerTemplate = consumerTemplate;
    }

    private Disposable createConsumer() {
        return consumerTemplate.receive()
                .delayUntil(receiverRecord -> reactiveProcessingService
                        .process(receiverRecord.value())
                        .onErrorResume(throwable -> {
                            log.error("Processing error", throwable);
                            return receiverRecord.receiverOffset().commit();
                        })
                        .then(Mono.defer(() -> {
                            log.info("Committing offset for {}", receiverRecord.value());
                            return receiverRecord.receiverOffset().commit();
                        })))
                .onErrorResume(throwable -> {
                    log.error("onErrorResume", throwable);
                    return Mono.empty();
                })
                .repeat()
                .subscribe();
    }

    @Override
    public void afterPropertiesSet(){
        if (consumer == null) {
            consumer = createConsumer();
        }
    }

    @Override
    public void destroy() {
        log.info("A 'destroy()' has been called.");
        if(consumer != null && !consumer.isDisposed()) {
            log.info("Destroying consumer - calling 'dispose()' on consumer.");
            consumer.dispose();
        }
    }
}
