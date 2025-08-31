package com.BlackstoneeIT.customer_management_service.controller;

import com.BlackstoneeIT.customer_management_service.dto.*;
import com.BlackstoneeIT.customer_management_service.services.AccountStatusService;
import com.BlackstoneeIT.customer_management_service.services.CustomerServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.UUID;
import com.BlackstoneeIT.customer_management_service.dto.AccountCreationResponse;
import com.BlackstoneeIT.customer_management_service.dto.AccountStatusDto;
import com.BlackstoneeIT.customer_management_service.dto.CreateCustomer;
import com.BlackstoneeIT.customer_management_service.dto.CustomerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customer Management", description = "APIs for managing customers and their bank accounts")
public class CustomerController {

    @Autowired
    private CustomerServices customerServices;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private AccountStatusService accountStatusService;

    @Operation(
            summary = "Create a new customer",
            description = "Registers a new customer in the banking system with validated information"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Customer created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CustomerResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or validation failed",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Customer with provided legal ID already exists",
                    content = @Content
            )
    })
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CustomerResponse> createCustomer(
            @Parameter(
                    description = "Customer creation request payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateCustomer.class))
            )
            @Valid @RequestBody CreateCustomer createCustomer) {

        CustomerResponse response = customerServices.createCustomer(createCustomer);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get customer by ID",
            description = "Retrieves customer details using their unique 7-digit identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Customer details retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CustomerResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer with the specified ID not found",
                    content = @Content
            )
    })
    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CustomerResponse> getCustomerById(
            @Parameter(
                    description = "7-digit customer identifier",
                    example = "1000001",
                    required = true
            )
            @PathVariable long id) {

        CustomerResponse response = customerServices.getCustomerById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get account creation status",
            description = "Check the status of an account creation request using the request ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Request status retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AccountStatusDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Request with specified ID not found",
                    content = @Content
            )
    })
    @GetMapping(
            value = "/requests/{requestId}/status",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<AccountStatusDto> getAccountRequestStatus(
            @Parameter(
                    description = "UUID of the account creation request",
                    example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                    required = true
            )
            @PathVariable String requestId) {

        AccountStatusDto status = accountStatusService.getRequestStatus(requestId);
        return ResponseEntity.ok(status);
    }

    @Operation(
            summary = "Request account creation",
            description = "Initiate the process to create a new bank account for an existing customer"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "202",
                    description = "Account creation request accepted and being processed",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AccountCreationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid account type or balance amount",
                    content = @Content
            )
    })
    @PostMapping(
            value = "/{customerId}/accounts",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<AccountCreationRequestResponse> createAccount(
            @Parameter(
                    description = "Customer ID for whom to create the account",
                    example = "1000001",
                    required = true
            )
            @PathVariable Long customerId,

            @Parameter(
                    description = "Type of account to create",
                    example = "SAVING",
                    required = true,
                    schema = @Schema(allowableValues = {"SAVING", "SALARY", "INVESTMENT"})
            )
            @RequestParam String accountType,

            @Parameter(
                    description = "Initial deposit amount",
                    example = "1000.00",
                    schema = @Schema(minimum = "0", defaultValue = "0.00")
            )
            @RequestParam(required = false, defaultValue = "0.00") BigDecimal initialBalance) {

        String requestId = UUID.randomUUID().toString();
        customerServices.requestAccountCreation(customerId, accountType, initialBalance, requestId);

        AccountCreationRequestResponse response = new AccountCreationRequestResponse(
                requestId,
                customerId,
                "PENDING",
                "Account creation request has been submitted for processing",
                "/api/v1/customers/requests/" + requestId + "/status"
        );

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @Operation(
            summary = "Test endpoint",
            description = "Development endpoint for testing purposes (not for production use)",
            hidden = true
    )
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        kafkaTemplate.send("test_topic", "Test message from Customer Service");
        return ResponseEntity.ok("Test message sent to Kafka");
    }
}