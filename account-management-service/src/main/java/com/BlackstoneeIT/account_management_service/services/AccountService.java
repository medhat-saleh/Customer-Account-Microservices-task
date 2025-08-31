package com.BlackstoneeIT.account_management_service.services;

import com.BlackstoneeIT.account_management_service.dto.AccountResponse;
import com.BlackstoneeIT.account_management_service.entity.Account;
import com.BlackstoneeIT.account_management_service.enums.AccountType;
import com.BlackstoneeIT.account_management_service.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
@Autowired
    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByCustomerId(Long customerId) {
        return accountRepository.findByCustomerId(customerId).stream()
                .map(AccountResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountById(Long accountId, Long customerId) {
        Account account = accountRepository.findByIdAndCustomerId(accountId, customerId)
                .orElseThrow(() -> new RuntimeException(
                        "Account not found with ID: " + accountId + " for customer: " + customerId));
        return AccountResponse.fromEntity(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(AccountResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public AccountResponse updateAccountBalance(Long accountId, Long customerId, Double newBalance) {
        Account account = accountRepository.findByIdAndCustomerId(accountId, customerId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setBalance(new BigDecimal(newBalance));
        Account updatedAccount = accountRepository.save(account);

        return AccountResponse.fromEntity(updatedAccount);
    }

    @Transactional
    public void deleteAccount(Long accountId, Long customerId) {
        Account account = accountRepository.findByIdAndCustomerId(accountId, customerId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        accountRepository.delete(account);
        log.info("Account deleted successfully: {}", accountId);
    }

    @Transactional
    public Account createAccount(Account account) {
        // Additional validation before saving
        if (account.getType() == AccountType.INVESTMENT &&
                account.getBalance().compareTo(new BigDecimal("10000.00")) < 0) {
            throw new IllegalArgumentException("Investment accounts require minimum balance of 10,000");
        }

        Account savedAccount = accountRepository.save(account);
        log.info("Account created successfully: {}", savedAccount.getId());
        return savedAccount;
    }
}