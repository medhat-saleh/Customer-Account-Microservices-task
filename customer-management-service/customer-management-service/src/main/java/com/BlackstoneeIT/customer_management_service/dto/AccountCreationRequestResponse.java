package com.BlackstoneeIT.customer_management_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Account creation response containing request details")
public class AccountCreationRequestResponse {

    @Schema(description = "Unique request ID for tracking", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String requestId;

    @Schema(description = "Customer ID", example = "1000001")
    private Long customerId;

    @Schema(description = "Current status of the request", example = "PENDING",
            allowableValues = {"PENDING", "PROCESSING", "SUCCESS", "FAILED"})
    private String status;

    @Schema(description = "Detailed status message", example = "Account creation request has been submitted")
    private String message;

    @Schema(description = "URL to check request status", example = "/api/v1/customers/requests/a1b2c3d4-e5f6-7890-abcd-ef1234567890/status")
    private String statusUrl;
}

