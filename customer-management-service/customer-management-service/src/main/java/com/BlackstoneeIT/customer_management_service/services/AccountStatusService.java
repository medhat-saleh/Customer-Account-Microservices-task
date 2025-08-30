package com.BlackstoneeIT.customer_management_service.services;

import com.BlackstoneeIT.customer_management_service.dto.AccountCreationResponse;
import com.BlackstoneeIT.customer_management_service.dto.AccountStatusDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountStatusService {

    private final KafkaTopicReaderService kafkaTopicReaderService;

    public AccountStatusDto getRequestStatus(String requestId) {
        // Read directly from Kafka topic
        AccountCreationResponse kafkaResponse = kafkaTopicReaderService.findResponseByRequestId(requestId);

        if (kafkaResponse == null) {
            // If not found in recent messages, try searching from beginning
            kafkaResponse = kafkaTopicReaderService.findResponseByRequestIdWithSeek(requestId);
        }

        if (kafkaResponse == null) {
            throw new RuntimeException("Request not found or still processing: " + requestId);
        }

        return convertToStatusDto(kafkaResponse);
    }

    public AccountCreationResponse getRawKafkaResponse(String requestId) {
        AccountCreationResponse response = kafkaTopicReaderService.findResponseByRequestId(requestId);
        if (response == null) {
            response = kafkaTopicReaderService.findResponseByRequestIdWithSeek(requestId);
        }

        if (response == null) {
            throw new RuntimeException("Kafka response not found for request: " + requestId);
        }

        return response;
    }

    public boolean requestExists(String requestId) {
        try {
            AccountCreationResponse response = kafkaTopicReaderService.findResponseByRequestId(requestId);
            return response != null;
        } catch (Exception e) {
            return false;
        }
    }

    private AccountStatusDto convertToStatusDto(AccountCreationResponse response) {
        return new AccountStatusDto(
                response.getRequestId(),
                response.getAccountId()!= null ? response.getAccountId().toString() : null,
                response.getCustomerId() != null ? response.getCustomerId().toString() : null,
                response.getStatus(),
                response.getMessage(),
                response.getErrorCode(),
                convertTimestampToLocalDateTime(response.getTimestamp())
        );
    }
    private LocalDateTime convertTimestampToLocalDateTime(Long timestamp) {
        if (timestamp == null) return null;
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                TimeZone.getDefault().toZoneId()
        );
    }
}