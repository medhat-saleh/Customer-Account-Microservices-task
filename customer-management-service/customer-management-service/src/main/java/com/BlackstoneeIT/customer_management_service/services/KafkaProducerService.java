package com.BlackstoneeIT.customer_management_service.services;

import com.BlackstoneeIT.customer_management_service.dto.AccountCreationEvent;
import com.BlackstoneeIT.customer_management_service.enums.CustomerType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, AccountCreationEvent> kafkaTemplate;
    private static final String ACCOUNT_CREATION_TOPIC = "account-creation-requests";

    public CompletableFuture<SendResult<String, AccountCreationEvent>> sendAccountCreationEvent(
            Long customerId, CustomerType customerType, String accountType, BigDecimal initialBalance,String requestId) {

        AccountCreationEvent event = new AccountCreationEvent(
                customerId,
                customerType,
                accountType,
                initialBalance,
                requestId,
                System.currentTimeMillis()
        );

        log.info("Publishing account creation event: {}", event);
        return kafkaTemplate.send(ACCOUNT_CREATION_TOPIC, event.getRequestId(), event);
    }
    @KafkaListener(topics = "account-creation-responses", groupId = "debug-group")
    public void debugRaw(String message) {
        log.info("DEBUG RAW message: {}", message);
    }
}