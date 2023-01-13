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
import reactor.util.retry.Retry;

@Component
@ConditionalOnProperty(value = "kafka-consumer-type", havingValue = "REACTIVE_DO_ON_NEXT")
public class ReactiveKafkaConsumerDoOnNext implements InitializingBean, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(ReactiveKafkaConsumerDoOnNext.class);

    private final ReactiveProcessingService reactiveProcessingService;
    private final ReactiveKafkaConsumerTemplate<String, String> consumerTemplate;
    private Disposable consumer;

    public ReactiveKafkaConsumerDoOnNext(ReactiveProcessingService reactiveProcessingService,
                                         ReactiveKafkaConsumerTemplate<String, String> consumerTemplate) {
        this.reactiveProcessingService = reactiveProcessingService;
        this.consumerTemplate = consumerTemplate;
    }

    private Disposable createConsumer() {
        return consumerTemplate.receive()
                .doOnNext(receiverRecord -> reactiveProcessingService
                        .process(receiverRecord.value())
                        .then(Mono.defer(() -> {
                            log.info("Commit offset for {}", receiverRecord.value());
                            receiverRecord.receiverOffset().acknowledge(); //BREAKPOINT (CommitFailedException) - timeout
                            return Mono.empty();
                        }))
                        .doOnError(throwable -> {
                            log.info("Processing error {}", receiverRecord.value(), throwable);
                            receiverRecord.receiverOffset().acknowledge(); //BREAKPOINT (CommitFailedException) - timeout
                        }).subscribe())
                .doOnError(throwable -> log.error("doOnError - retry", throwable))
                .retryWhen(Retry.max(3).transientErrors(true))
                .onErrorResume(throwable -> {
                    log.error("onErrorResume", throwable);
                    return Mono.empty();
                })
                .repeat()
                .subscribe();
    }

    @Override
    public void afterPropertiesSet() {
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
