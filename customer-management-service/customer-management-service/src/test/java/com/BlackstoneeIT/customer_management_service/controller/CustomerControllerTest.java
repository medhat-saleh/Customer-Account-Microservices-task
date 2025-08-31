package com.BlackstoneeIT.customer_management_service.controller;

import com.BlackstoneeIT.customer_management_service.dto.*;
import com.BlackstoneeIT.customer_management_service.enums.CustomerType;
import com.BlackstoneeIT.customer_management_service.exception.GlobalExceptionHandler;
import com.BlackstoneeIT.customer_management_service.services.AccountStatusService;
import com.BlackstoneeIT.customer_management_service.services.CustomerServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.apache.tomcat.jni.Buffer.address;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerServices customerServices;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private AccountStatusService accountStatusService;

    @InjectMocks
    private CustomerController customerController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private CreateCustomer createCustomer;
    private CustomerResponse customerResponse;
    private AccountStatusDto accountStatusDto;


        @BeforeEach
        void setUp() {
            mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
            objectMapper = new ObjectMapper();
            GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

            // Setup MockMvc with both controller AND exception handler
            mockMvc = MockMvcBuilders.standaloneSetup(customerController)
                    .setControllerAdvice(globalExceptionHandler) // Add this line
                    .build();
            // Setup test data using Builder pattern
            createCustomer = CreateCustomer.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("john.doe@example.com")
                    .phoneNumber("+1234567890")
                    .legalID("A123456789")
                    .address("23 Main St, City, Country")
                    .customerType(CustomerType.CORPORATE)
                    .build();

            customerResponse = CustomerResponse.builder()
                    .customerId(1000001L)
                    .firstName("John")
                    .lastName("Doe")
                    .email("john.doe@example.com")
                    .phoneNumber("+1234567890")
                    .legalID("A123456789")
                    .build();

            accountStatusDto = AccountStatusDto.builder()
                    .requestId(UUID.randomUUID().toString())
                    .status("PROCESSING")
                    .message("Account creation is in progress")
                    .accountId(5000001L)
                    .timestamp(LocalDateTime.now())
                    .build();
        }

    @Test
    void createCustomer_ValidRequest_ReturnsCreatedResponse() throws Exception {
        // Arrange
        when(customerServices.createCustomer(any(CreateCustomer.class))).thenReturn(customerResponse);

        // Act & Assert using MockMvc
        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCustomer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value(1000001L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        // Verify service method was called
        verify(customerServices, times(1)).createCustomer(any(CreateCustomer.class));
    }

    @Test
    void getCustomerById_ValidId_ReturnsCustomerResponse() throws Exception {
        // Arrange
        long customerId = 1000001L;
        when(customerServices.getCustomerById(customerId)).thenReturn(customerResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/customers/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        // Verify service method was called
        verify(customerServices, times(1)).getCustomerById(customerId);
    }

    @Test
    void getCustomerById_NonExistentId_ReturnsNotFound() throws Exception {
        // Arrange
        long nonExistentId = 9999999L;
        when(customerServices.getCustomerById(nonExistentId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/v1/customers/{id}", nonExistentId))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        // Verify service method was called
        verify(customerServices, times(1)).getCustomerById(nonExistentId);
    }

    @Test
    void getAccountRequestStatus_ValidRequestId_ReturnsStatus() throws Exception {
        // Arrange
        String requestId = UUID.randomUUID().toString();
        when(accountStatusService.getRequestStatus(requestId)).thenReturn(accountStatusDto);

        // Act & Assert
        mockMvc.perform(get("/api/v1/customers/requests/{requestId}/status", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PROCESSING"))
                .andExpect(jsonPath("$.message").exists());

        // Verify service method was called
        verify(accountStatusService, times(1)).getRequestStatus(requestId);
    }

    @Test
    void getAccountRequestStatus_NonExistentRequestId_ReturnsNotFound() throws Exception {
        // Arrange
        String nonExistentRequestId = "non-existent-uuid";
        when(accountStatusService.getRequestStatus(nonExistentRequestId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/v1/customers/requests/{requestId}/status", nonExistentRequestId))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        // Verify service method was called
        verify(accountStatusService, times(1)).getRequestStatus(nonExistentRequestId);
    }

    @Test
    void createAccount_ValidRequest_ReturnsAcceptedResponse() throws Exception {
        // Arrange
        Long customerId = 1000001L;
        String accountType = "SAVING";
        BigDecimal initialBalance = new BigDecimal("1000.00");

        // Use doNothing for void methods with any() matchers
        doNothing().when(customerServices).requestAccountCreation(
                any(Long.class), any(String.class), any(BigDecimal.class), any(String.class));

        // Act & Assert
        mockMvc.perform(post("/api/v1/customers/{customerId}/accounts", customerId)
                        .param("accountType", accountType)
                        .param("initialBalance", initialBalance.toString()))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.statusUrl").exists());

        // Verify service method was called
        verify(customerServices, times(1)).requestAccountCreation(
                any(Long.class), any(String.class), any(BigDecimal.class), any(String.class));
    }

    @Test
    void createAccount_WithDefaultBalance_ReturnsAcceptedResponse() throws Exception {
        // Arrange
        Long customerId = 1000001L;
        String accountType = "SAVING";

        // Use doNothing with any() matchers
        doNothing().when(customerServices).requestAccountCreation(
                any(Long.class), any(String.class), any(BigDecimal.class), any(String.class));

        // Act & Assert
        mockMvc.perform(post("/api/v1/customers/{customerId}/accounts", customerId)
                        .param("accountType", accountType))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.status").value("PENDING"));

        // Verify service method was called
        verify(customerServices, times(1)).requestAccountCreation(
                any(Long.class), any(String.class), any(BigDecimal.class), any(String.class));
    }

    @Test
    void testEndpoint_SendsKafkaMessage_ReturnsOk() throws Exception {
        // Arrange
        doReturn(null).when(kafkaTemplate).send(eq("test_topic"), anyString());

        // Act & Assert
        mockMvc.perform(get("/api/v1/customers/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Test message sent to Kafka"));

        // Verify Kafka message was sent
        verify(kafkaTemplate, times(1)).send(eq("test_topic"), anyString());
    }

    @Test
    void createCustomer_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        // Arrange
        when(customerServices.createCustomer(any(CreateCustomer.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCustomer)))
                .andExpect(status().isInternalServerError());

        // Verify service method was called
        verify(customerServices, times(1)).createCustomer(any(CreateCustomer.class));
    }

    @Test
    void createAccount_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        // Arrange
        Long customerId = 1000001L;
        String accountType = "SAVING";
        BigDecimal initialBalance = new BigDecimal("1000.00");

        doThrow(new RuntimeException("Service unavailable"))
                .when(customerServices).requestAccountCreation(any(), any(), any(), any());

        // Act & Assert
        mockMvc.perform(post("/api/v1/customers/{customerId}/accounts", customerId)
                        .param("accountType", accountType)
                        .param("initialBalance", initialBalance.toString()))
                .andExpect(status().isInternalServerError());

        // Verify service method was called
        verify(customerServices, times(1)).requestAccountCreation(any(), any(), any(), any());
    }

    // Direct method call tests (without MockMvc)
    @Test
    void createAccount_ResponseStructure_ContainsCorrectFields() {
        // Arrange
        Long customerId = 1000001L;

        // Use doNothing for void method
        doNothing().when(customerServices).requestAccountCreation(any(), any(), any(), any());

        // Act
        ResponseEntity<AccountCreationRequestResponse> response = customerController.createAccount(
                customerId, "SAVING", new BigDecimal("500.00"));

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

        AccountCreationRequestResponse body = response.getBody();
        assertNotNull(body);
        assertNotNull(body.getRequestId());
        assertEquals(customerId, body.getCustomerId());
        assertEquals("PENDING", body.getStatus());
        assertTrue(body.getMessage().contains("submitted for processing"));
        assertTrue(body.getStatusUrl().contains(body.getRequestId()));
    }

    @Test
    void createCustomer_ResponseStructure_ContainsCorrectFields() {
        // Arrange
        when(customerServices.createCustomer(any(CreateCustomer.class))).thenReturn(customerResponse);

        // Act
        ResponseEntity<CustomerResponse> response = customerController.createCustomer(createCustomer);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(customerResponse, response.getBody());
    }
}