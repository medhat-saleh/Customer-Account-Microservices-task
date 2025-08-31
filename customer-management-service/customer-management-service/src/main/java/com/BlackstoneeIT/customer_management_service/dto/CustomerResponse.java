package com.BlackstoneeIT.customer_management_service.dto;

import com.BlackstoneeIT.customer_management_service.enums.CustomerType;
import lombok.Builder;

@Builder
public record CustomerResponse(Long customerId, String legalID, String firstName, String lastName, String email, String phoneNumber, String address, CustomerType customerType) {
}
