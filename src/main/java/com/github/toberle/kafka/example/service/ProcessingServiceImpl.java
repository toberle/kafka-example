package com.github.toberle.kafka.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProcessingServiceImpl implements ProcessingService {
    private static final Logger log = LoggerFactory.getLogger(ProcessingServiceImpl.class);

    @Override
    public void process(String value) {
        if (value.startsWith("ERROR")) {
            simulateErrorDuringProcessing(value);
        } else if (value.startsWith("ERROR_RETRY")) {
            simulateErrorWithRetryDuringProcessing(value);
        } else if (value.startsWith("LONG_PROCESSING")) {
            simulateLongProcessing(value);
        } else {
            simulateProcessing(value);
        }

    }

    private void simulateErrorWithRetryDuringProcessing(String value) {
        log.warn("Not supported in this processing service - value {}", value);
    }

    private void simulateProcessing(String value) {
        log.info("Consumer processing started: {}", value);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("Consumer processing successfully finished: {}", value);
    }

    private void simulateErrorDuringProcessing(String value) {
        log.info("Processing will fail with error (exception)...");
        throw new RuntimeException("Processing failed with exception for: " + value);
    }

    private void simulateLongProcessing(String value) {
        log.info("Consumer long processing started: {}", value);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("Consumer long processing successfully finished: {}", value);
    }
}
