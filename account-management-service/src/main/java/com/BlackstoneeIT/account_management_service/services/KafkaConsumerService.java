package com.BlackstoneeIT.account_management_service.services;


import com.BlackstoneeIT.account_management_service.dto.AccountCreationEvent;
import com.BlackstoneeIT.account_management_service.dto.AccountCreationResponse;
import com.BlackstoneeIT.account_management_service.entity.Account;
import com.BlackstoneeIT.account_management_service.enums.AccountStatus;
import com.BlackstoneeIT.account_management_service.enums.AccountType;
import com.BlackstoneeIT.account_management_service.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final AccountRepository accountRepository;
    private final AccountValidationService validationService;
    private final KafkaTemplate<String, AccountCreationResponse> kafkaTemplate;
    private static final String ACCOUNT_CREATION_RESPONSE_TOPIC = "account-creation-responses";

    @KafkaListener(
            topics = "${kafka.topics.account-creation-requests}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void consumeAccountCreationRequest(@Payload AccountCreationEvent event) {
        log.info("Received account creation request: {}", event);

        try {
            // 1. Validate the account creation request
            validationService.validateAccountCreation(
                    event.getCustomerId(),
                    event.getCustomerType(),
                    event.getAccountType(),
                    event.getInitialBalance()
            );

            // 2. Create the account
            Account account = createAccountFromEvent(event);

            // 3. Save the account
            Account savedAccount = accountRepository.save(account);

            // 4. Send success response
            sendAccountCreationResponse(
                    event,
                    savedAccount.getId(),
                    "SUCCESS",
                    "Account created successfully"
            );

            log.info("Account created successfully: {}", savedAccount.getId());

        } catch (IllegalArgumentException e) {
            // Business validation failed
            log.warn("Account creation validation failed for customer {}: {}",
                    event.getCustomerId(), e.getMessage());
            sendAccountCreationResponse(event, null, "VALIDATION_FAILED", e.getMessage());

        } catch (Exception e) {
            // Technical failure
            log.error("Failed to create account for customer: {}", event.getCustomerId(), e);
            sendAccountCreationResponse(event, null, "FAILED", "Technical error: " + e.getMessage());
        }
    }

    private Account createAccountFromEvent(AccountCreationEvent event) {
        Account account = new Account();
        account.setCustomerId(event.getCustomerId());
        account.setType(AccountType.valueOf(event.getAccountType()));
        account.setBalance(event.getInitialBalance() != null ? event.getInitialBalance() : BigDecimal.ZERO);
        account.setStatus(AccountStatus.ACTIVE);
        account.setId(generateSequentialAccountId(event.getCustomerId()));
        return account;
    }

//    private Long generateAccountId(Long customerId) {
//        // Generate 10-digit account ID: first 7 digits = customer ID, last 3 random
//        String customerIdStr = String.valueOf(customerId);
//        String randomSuffix = String.format("%03d", (int) (Math.random() * 1000));
//        return Long.parseLong(customerIdStr + randomSuffix);
//    }
private Long generateSequentialAccountId(Long customerId) {
    // Find the highest account ID for this customer
    Long maxAccountId = accountRepository.findMaxAccountIdByCustomerId(customerId)
            .orElse(customerId * 1000L); // Start from customerId + 000

    // Generate next sequential ID
    Long nextAccountId = maxAccountId + 1;

    // Verify it follows the pattern (customer ID + 3 digits)
    Long expectedCustomerIdPart = nextAccountId / 1000;
    if (!expectedCustomerIdPart.equals(customerId)) {
        // If we exceed 999, wrap around to 000
        nextAccountId = customerId * 1000L;
    }

    // Check if this ID already exists (in case of wrap-around)
    if (accountRepository.existsById(nextAccountId)) {
        throw new IllegalStateException("Account ID space exhausted for customer: " + customerId);
    }

    return nextAccountId;
}

    private void sendAccountCreationResponse(AccountCreationEvent event, Long accountId,
                                             String status, String message) {
        AccountCreationResponse response = new AccountCreationResponse(
                event.getRequestId(),
                accountId,
                event.getCustomerId(),
                status,
                message,
                getErrorCode(status),
                System.currentTimeMillis()
        );

        kafkaTemplate.send(ACCOUNT_CREATION_RESPONSE_TOPIC, event.getRequestId(), response);
        log.info("Sent account creation response: {}", response);
    }

    private String getErrorCode(String status) {
        return "VALIDATION_FAILED".equals(status) ? "VALIDATION_ERROR" :
                "FAILED".equals(status) ? "TECHNICAL_ERROR" : null;
    }
}
