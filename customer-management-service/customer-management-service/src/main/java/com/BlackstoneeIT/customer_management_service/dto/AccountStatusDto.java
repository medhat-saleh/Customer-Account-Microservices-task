package com.BlackstoneeIT.customer_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AccountStatusDto {
    private String requestId;
    private String accountId;
    private String customerId;
    private String status;
    private String message;
    private String errorCode;
    private LocalDateTime timestamp;
}