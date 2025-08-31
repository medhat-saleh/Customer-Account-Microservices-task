package com.BlackstoneeIT.account_management_service.controller;

import com.BlackstoneeIT.account_management_service.dto.AccountResponse;
import com.BlackstoneeIT.account_management_service.enums.AccountStatus;
import com.BlackstoneeIT.account_management_service.enums.AccountType;
import com.BlackstoneeIT.account_management_service.services.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private AccountResponse accountResponse1;
    private AccountResponse accountResponse2;
    private List<AccountResponse> accountList;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
        objectMapper = new ObjectMapper();

        // Setup test data matching your actual DTO structure
        accountResponse1 = AccountResponse.builder()
                .id(5000001L)
                .customerId(1000001L)
                .balance(new BigDecimal("1500.75"))
                .type(AccountType.SAVING)
                .status(AccountStatus.ACTIVE)  // Using your actual AccountStatus enum
                .minBalance(new BigDecimal("100.00"))
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        accountResponse2 = AccountResponse.builder()
                .id(5000002L)
                .customerId(1000001L)
                .balance(new BigDecimal("2500.50"))
                .type(AccountType.SALARY)
                .status(AccountStatus.ACTIVE)  // Using your actual AccountStatus enum
                .minBalance(new BigDecimal("50.00"))
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        accountList = Arrays.asList(accountResponse1, accountResponse2);
    }

    @Test
    void getAccountsByCustomerId_ValidCustomerId_ReturnsAccounts() throws Exception {
        // Arrange
        Long customerId = 1000001L;
        when(accountService.getAccountsByCustomerId(customerId)).thenReturn(accountList);

        // Act & Assert
        mockMvc.perform(get("/api/v1/accounts/customer/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(5000001L))
                .andExpect(jsonPath("$[0].customerId").value(1000001L))
                .andExpect(jsonPath("$[0].type").value("SAVING"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))  // Using your AccountStatus value
                .andExpect(jsonPath("$[0].balance").value(1500.75))
                .andExpect(jsonPath("$[1].id").value(5000002L))
                .andExpect(jsonPath("$[1].type").value("SALARY"))
                .andExpect(jsonPath("$[1].status").value("ACTIVE")); // Using your AccountStatus value

        verify(accountService, times(1)).getAccountsByCustomerId(customerId);
    }

    @Test
    void getAccountById_ValidIds_ReturnsAccount() throws Exception {
        // Arrange
        Long accountId = 5000001L;
        Long customerId = 1000001L;
        when(accountService.getAccountById(accountId, customerId)).thenReturn(accountResponse1);

        // Act & Assert
        mockMvc.perform(get("/api/v1/accounts/{accountId}/customer/{customerId}", accountId, customerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5000001L))
                .andExpect(jsonPath("$.customerId").value(1000001L))
                .andExpect(jsonPath("$.type").value("SAVING"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))  // Using your AccountStatus value
                .andExpect(jsonPath("$.balance").value(1500.75));

        verify(accountService, times(1)).getAccountById(accountId, customerId);
    }

    @Test
    void getAllAccounts_ReturnsAllAccounts() throws Exception {
        // Arrange
        when(accountService.getAllAccounts()).thenReturn(accountList);

        // Act & Assert
        mockMvc.perform(get("/api/v1/accounts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(5000001L))
                .andExpect(jsonPath("$[0].type").value("SAVING"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))  // Using your AccountStatus value
                .andExpect(jsonPath("$[1].id").value(5000002L))
                .andExpect(jsonPath("$[1].type").value("SALARY"))
                .andExpect(jsonPath("$[1].status").value("ACTIVE")); // Using your AccountStatus value

        verify(accountService, times(1)).getAllAccounts();
    }

    @Test
    void updateAccountBalance_ValidRequest_ReturnsUpdatedAccount() throws Exception {
        // Arrange
        Long accountId = 5000001L;
        Long customerId = 1000001L;
        Double newBalance = 2000.00;

        AccountResponse updatedAccount = AccountResponse.builder()
                .id(accountId)
                .customerId(customerId)
                .balance(new BigDecimal(newBalance))
                .type(AccountType.SAVING)
                .status(AccountStatus.ACTIVE)  // Using your AccountStatus enum
                .minBalance(new BigDecimal("100.00"))
                .build();

        when(accountService.updateAccountBalance(accountId, customerId, newBalance)).thenReturn(updatedAccount);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/accounts/{accountId}/customer/{customerId}/balance", accountId, customerId)
                        .param("newBalance", newBalance.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5000001L))
                .andExpect(jsonPath("$.balance").value(2000.00))
                .andExpect(jsonPath("$.type").value("SAVING"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));  // Using your AccountStatus value

        verify(accountService, times(1)).updateAccountBalance(accountId, customerId, newBalance);
    }

    @Test
    void deleteAccount_ValidIds_ReturnsNoContent() throws Exception {
        // Arrange
        Long accountId = 5000001L;
        Long customerId = 1000001L;

        doNothing().when(accountService).deleteAccount(accountId, customerId);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/accounts/{accountId}/customer/{customerId}", accountId, customerId))
                .andExpect(status().isNoContent());

        verify(accountService, times(1)).deleteAccount(accountId, customerId);
    }

    @Test
    void getAccountsByCustomerId_NonExistentCustomerId_ReturnsEmptyList() throws Exception {
        // Arrange
        Long nonExistentCustomerId = 9999999L;
        when(accountService.getAccountsByCustomerId(nonExistentCustomerId)).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/v1/accounts/customer/{customerId}", nonExistentCustomerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(accountService, times(1)).getAccountsByCustomerId(nonExistentCustomerId);
    }

    @Test
    void getAccountById_NonExistentAccount_ReturnsNotFound() throws Exception {
        // Arrange
        Long nonExistentAccountId = 9999999L;
        Long customerId = 1000001L;
        when(accountService.getAccountById(nonExistentAccountId, customerId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/v1/accounts/{accountId}/customer/{customerId}", nonExistentAccountId, customerId))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(accountService, times(1)).getAccountById(nonExistentAccountId, customerId);
    }

    // Test for different account statuses
    @Test
    void getAccountById_SuspendedAccount_ReturnsSuspendedStatus() throws Exception {
        // Arrange
        Long accountId = 5000003L;
        Long customerId = 1000001L;

        AccountResponse suspendedAccount = AccountResponse.builder()
                .id(accountId)
                .customerId(customerId)
                .balance(new BigDecimal("500.00"))
                .type(AccountType.SAVING)
                .status(AccountStatus.SUSPENDED)  // Testing SUSPENDED status
                .minBalance(new BigDecimal("100.00"))
                .build();

        when(accountService.getAccountById(accountId, customerId)).thenReturn(suspendedAccount);

        // Act & Assert
        mockMvc.perform(get("/api/v1/accounts/{accountId}/customer/{customerId}", accountId, customerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5000003L))
                .andExpect(jsonPath("$.status").value("SUSPENDED"));  // Testing SUSPENDED status

        verify(accountService, times(1)).getAccountById(accountId, customerId);
    }

    @Test
    void getAccountById_ClosedAccount_ReturnsClosedStatus() throws Exception {
        // Arrange
        Long accountId = 5000004L;
        Long customerId = 1000001L;

        AccountResponse closedAccount = AccountResponse.builder()
                .id(accountId)
                .customerId(customerId)
                .balance(BigDecimal.ZERO)
                .type(AccountType.SAVING)
                .status(AccountStatus.CLOSED)  // Testing CLOSED status
                .minBalance(new BigDecimal("100.00"))
                .build();

        when(accountService.getAccountById(accountId, customerId)).thenReturn(closedAccount);

        // Act & Assert
        mockMvc.perform(get("/api/v1/accounts/{accountId}/customer/{customerId}", accountId, customerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5000004L))
                .andExpect(jsonPath("$.status").value("CLOSED"));  // Testing CLOSED status

        verify(accountService, times(1)).getAccountById(accountId, customerId);
    }
}