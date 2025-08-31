package com.BlackstoneeIT.account_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreationEvent {
    private Long customerId;
    private String customerType;
    private String accountType;
    private BigDecimal initialBalance;
    private String requestId;

}