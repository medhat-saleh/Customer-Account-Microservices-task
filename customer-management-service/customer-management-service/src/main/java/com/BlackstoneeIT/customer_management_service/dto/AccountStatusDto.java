package com.BlackstoneeIT.customer_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class AccountStatusDto {
    private String requestId;
    private Long accountId;
    private Long customerId;
    private String status;
    private String message;
    private String errorCode;
    private LocalDateTime timestamp;
}