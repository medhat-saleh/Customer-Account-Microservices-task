

package com.BlackstoneeIT.account_management_service.controller;

import com.BlackstoneeIT.account_management_service.dto.AccountResponse;
import com.BlackstoneeIT.account_management_service.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Account Management", description = "APIs for managing customer bank accounts")
public class AccountController {

    private final AccountService accountService;
    private static final Logger log = LogManager.getLogger(AccountService.class);

    @Operation(
            summary = "Get accounts by customer ID",
            description = "Retrieves all bank accounts associated with a specific customer",
            parameters = {
                    @Parameter(
                            name = "customerId",
                            description = "Unique identifier of the customer",
                            example = "1000001",
                            required = true
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Accounts retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = AccountResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"error\": \"Customer not found with ID: 1000001\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"error\": \"Internal server error occurred\"}")
                    )
            )
    })
    @GetMapping(value = "/customer/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AccountResponse>> getAccountsByCustomerId(
            @Parameter(description = "Customer ID must be a positive number", example = "1000001")
            @PathVariable @Min(1) Long customerId) {


        log.info("Fetching accounts for customer ID: {}", customerId);

        try {
            List<AccountResponse> accounts = accountService.getAccountsByCustomerId(customerId);

            if (accounts.isEmpty()) {
                log.info("No accounts found for customer ID: {}", customerId);
                return ResponseEntity.ok(Collections.emptyList());
            }

            log.info("Found {} accounts for customer ID: {}", accounts.size(), customerId);
            return ResponseEntity.ok(accounts);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid request for customer ID: {} - {}", customerId, e.getMessage());
            return ResponseEntity.badRequest().body(Collections.emptyList());

        } catch (Exception e) {
            log.error("Error fetching accounts for customer ID: {}", customerId, e);
            return ResponseEntity.internalServerError().body(Collections.emptyList());
        }
    }
    @Operation(
            summary = "Get account by ID and customer ID",
            description = "Retrieves a specific account by its ID and verifies customer ownership",
            parameters = {
                    @Parameter(
                            name = "accountId",
                            description = "Unique identifier of the account",
                            example = "5000001",
                            required = true
                    ),
                    @Parameter(
                            name = "customerId",
                            description = "Unique identifier of the customer",
                            example = "1000001",
                            required = true
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Account retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AccountResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account or customer not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(name = "Account not found", value = "{\"error\": \"Account not found with ID: 5000001\"}"),
                                    @ExampleObject(name = "Customer not found", value = "{\"error\": \"Customer not found with ID: 1000001\"}"),
                                    @ExampleObject(name = "Access denied", value = "{\"error\": \"Account does not belong to the specified customer\"}")
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @GetMapping("/{accountId}/customer/{customerId}")
    public ResponseEntity<AccountResponse> getAccountById(
            @PathVariable Long accountId,
            @PathVariable Long customerId) {
        AccountResponse account = accountService.getAccountById(accountId, customerId);
        return ResponseEntity.ok(account);
    }

    @Operation(
            summary = "Get all accounts",
            description = "Retrieves all bank accounts in the system (Admin functionality)",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "All accounts retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = AccountResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Authentication required",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"error\": \"Full authentication is required to access this resource\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Insufficient permissions",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"error\": \"Access denied. Admin privileges required\"}")
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        List<AccountResponse> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @Operation(
            summary = "Update account balance",
            description = "Updates the balance of a specific account for a customer",
            parameters = {
                    @Parameter(
                            name = "accountId",
                            description = "Unique identifier of the account",
                            example = "5000001",
                            required = true
                    ),
                    @Parameter(
                            name = "customerId",
                            description = "Unique identifier of the customer",
                            example = "1000001",
                            required = true
                    ),
                    @Parameter(
                            name = "newBalance",
                            description = "New balance amount to set",
                            example = "1500.75",
                            required = true
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Account balance updated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AccountResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid balance amount",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"error\": \"Balance cannot be negative\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account or customer not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @PatchMapping("/{accountId}/customer/{customerId}/balance")
    public ResponseEntity<AccountResponse> updateAccountBalance(
            @PathVariable Long accountId,
            @PathVariable Long customerId,
            @RequestParam Double newBalance) {
        AccountResponse account = accountService.updateAccountBalance(accountId, customerId, newBalance);
        return ResponseEntity.ok(account);
    }

    @Operation(
            summary = "Delete account",
            description = "Deletes a specific account for a customer",
            parameters = {
                    @Parameter(
                            name = "accountId",
                            description = "Unique identifier of the account to delete",
                            example = "5000001",
                            required = true
                    ),
                    @Parameter(
                            name = "customerId",
                            description = "Unique identifier of the customer",
                            example = "1000001",
                            required = true
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Account deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account or customer not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"error\": \"Account not found with ID: 5000001\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Account has active transactions or positive balance",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"error\": \"Cannot delete account with positive balance\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @DeleteMapping("/{accountId}/customer/{customerId}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable Long accountId,
            @PathVariable Long customerId) {
        accountService.deleteAccount(accountId, customerId);
        return ResponseEntity.noContent().build();
    }
}