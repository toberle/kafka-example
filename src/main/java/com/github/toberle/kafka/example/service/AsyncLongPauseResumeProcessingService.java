package com.github.toberle.kafka.example.service;

import org.springframework.kafka.support.Acknowledgment;

public interface AsyncLongPauseResumeProcessingService {

    void process(String message, Acknowledgment ack);
}
