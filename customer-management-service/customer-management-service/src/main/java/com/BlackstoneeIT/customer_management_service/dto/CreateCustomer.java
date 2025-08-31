package com.BlackstoneeIT.customer_management_service.dto;

import com.BlackstoneeIT.customer_management_service.enums.CustomerType;
import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record CreateCustomer(
                              @NotBlank(message = "Legal ID is required")
                              @Size(min = 5, max = 20, message = "Legal ID must be between 5-20 characters")
                              String legalID,

                              @NotBlank(message = "First name is required")
                              @Size(min = 2, max = 50, message = "First name must be between 2-50 characters")
                              @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "First name must contain only letters and spaces")
                              String firstName,

                              @Size(max = 50, message = "Last name must be less than 50 characters")
                              @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Last name must contain only letters and spaces")
                              String lastName,

                              @NotBlank(message = "Email is required")
                              @Email(message = "Email must be a valid format")
                              @Size(max = 100, message = "Email must be less than 100 characters")
                              String email,

                              @NotBlank(message = "Phone number is required")
                              @Pattern(regexp = "^[\\+]?[0-9\\-\\(\\)\\s]{10,15}$",
                                      message = "Phone number must be 10-15 digits and may include +, -, (, )")
                              String phoneNumber,

                              @NotBlank(message = "Address is required")
                              @Size(min = 5, max = 200, message = "Address must be between 5-200 characters")
                              String address,

                              @NotNull(message = "Customer type is required")
                              CustomerType customerType
) {}