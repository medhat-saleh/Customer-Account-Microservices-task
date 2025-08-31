package com.BlackstoneeIT.account_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreationResponse {
    private String requestId;
    private Long accountId;
    private Long customerId;
    private String status; // SUCCESS, VALIDATION_FAILED, FAILED
    private String message;
    private String errorCode;
    private Long timestamp;

    public static AccountCreationResponse success(String requestId, Long accountId, Long customerId) {
        return new AccountCreationResponse(
                requestId, accountId, customerId, "SUCCESS", "Account created successfully", null, System.currentTimeMillis()
        );
    }

    public static AccountCreationResponse validationFailed(String requestId, Long customerId, String message) {
        return new AccountCreationResponse(
                requestId, null, customerId, "VALIDATION_FAILED", message, "VALIDATION_ERROR", System.currentTimeMillis()
        );
    }

    public static AccountCreationResponse failed(String requestId, Long customerId, String message) {
        return new AccountCreationResponse(
                requestId, null, customerId, "FAILED", message, "TECHNICAL_ERROR", System.currentTimeMillis()
        );
    }
}