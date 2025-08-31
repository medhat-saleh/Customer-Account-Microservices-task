package com.BlackstoneeIT.customer_management_service.dto;

import com.BlackstoneeIT.customer_management_service.enums.CustomerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreationEvent {
    private Long customerId;
    private CustomerType customerType;
    private String type; // SAVING, SALARY, INVESTMENT
    private BigDecimal initialBalance;
    private String requestId; // For tracking the request
    private Long timestamp;

}