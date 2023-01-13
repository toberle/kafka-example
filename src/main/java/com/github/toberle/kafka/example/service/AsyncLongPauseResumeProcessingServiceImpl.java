package com.github.toberle.kafka.example.service;

import com.github.toberle.kafka.example.manager.KafkaPauseResumeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class AsyncLongPauseResumeProcessingServiceImpl implements AsyncLongPauseResumeProcessingService {

    private static final Logger log = LoggerFactory.getLogger(AsyncLongPauseResumeProcessingServiceImpl.class);

    private final KafkaPauseResumeManager kafkaPauseResumeManager;

    @Value("${listener.pause-resume.long-processing-time:10s}")
    private Duration longProcessingTime;


    public AsyncLongPauseResumeProcessingServiceImpl(KafkaPauseResumeManager kafkaPauseResumeManager) {
        this.kafkaPauseResumeManager = kafkaPauseResumeManager;
    }

    @Async
    @Override
    public void process(String message, Acknowledgment ack) {
        try {
            log.info("Starting long processing of '{}', seconds: {}", message, longProcessingTime.getSeconds());
            TimeUnit.SECONDS.sleep(longProcessingTime.getSeconds());
            log.info("The long processing is finished for '{}'", message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted exception occurred for '{}'", message, e);
        } catch (Exception e) {
            log.error("The error occurred for '{}'", message, e);
        } finally {
            log.info("Ack and resuming consumer for '{}'", message);
            ack.acknowledge();
            kafkaPauseResumeManager.resume();
            log.info("Ack and resume was done for '{}'", message);
        }
    }
}
