package com.github.szabodzsi.kibit.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionNotificationServiceImpl implements TransactionNotificationService {

    private static final String KAFKA_TOPIC = "transaction_notifications";
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void sendNotification(String message) {
        kafkaTemplate.send(KAFKA_TOPIC, message);
    }
}
