package com.BlackstoneeIT.account_management_service.services;

import com.BlackstoneeIT.account_management_service.enums.AccountType;
import com.BlackstoneeIT.account_management_service.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountValidationService {

    private final AccountRepository accountRepository;

    public void validateAccountCreation(Long customerId, String customerType,
                                        String accountType, BigDecimal initialBalance) {

        log.info("Validating account creation for customer: {}, type: {}", customerId, accountType);

        // 1. Validate account type
        validateAccountType(accountType);

        // 2. Validate customer can have up to 10 accounts
        validateAccountLimit(customerId);

        // 3. Validate retail customers can only have saving accounts
        validateRetailCustomerRestrictions(customerType, accountType);

        // 4. Validate only one salary account per customer
        validateSalaryAccountLimit(customerId, accountType);

        // 5. Validate investment account minimum balance
        validateInvestmentAccountRequirements(accountType, initialBalance);

        log.info("Account validation passed for customer: {}", customerId);
    }

    private void validateAccountType(String accountType) {
        try {
            AccountType.valueOf(accountType);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid account type: " + accountType +
                    ". Valid types are: SAVING, SALARY, INVESTMENT");
        }
    }

    private void validateAccountLimit(Long customerId) {
        long accountCount = accountRepository.countByCustomerId(customerId);
        if (accountCount >= 10) {
            throw new IllegalArgumentException("Customer can have maximum 10 accounts. Current count: " + accountCount);
        }
    }

    private void validateRetailCustomerRestrictions(String customerType, String accountType) {
        if ("RETAIL".equals(customerType) && !"SAVING".equals(accountType)) {
            throw new IllegalArgumentException("Retail customers can only have SAVING accounts. Requested: " + accountType);
        }
    }

    private void validateSalaryAccountLimit(Long customerId, String accountType) {
        if ("SALARY".equals(accountType)) {
            boolean hasSalaryAccount = accountRepository.existsByCustomerIdAndType(
                    customerId,AccountType.SALARY);
            if (hasSalaryAccount) {
                throw new IllegalArgumentException("Customer can have only one SALARY account");
            }
        }
    }

    private void validateInvestmentAccountRequirements(String accountType, BigDecimal initialBalance) {
        if ("INVESTMENT".equals(accountType)) {
            BigDecimal minInvestmentBalance = new BigDecimal("10000.00");
            if (initialBalance == null || initialBalance.compareTo(minInvestmentBalance) < 0) {
                throw new IllegalArgumentException(
                        "INVESTMENT accounts must have minimum balance of 10,000. Provided: " +
                                (initialBalance != null ? initialBalance : "null"));
            }
        }
    }
}
