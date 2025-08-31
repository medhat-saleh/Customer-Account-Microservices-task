package com.BlackstoneeIT.account_management_service.entity;
import com.BlackstoneeIT.account_management_service.enums.AccountStatus;
import com.BlackstoneeIT.account_management_service.enums.AccountType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts", schema = "account_db")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {


    @Id
    @Column(name = "id")
    @NotNull(message = "Account ID is required")
    private Long id;

    @Column(name = "customer_id")
    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @Column(name = "balance")
    @DecimalMin(value = "0.00", message = "Balance cannot be negative")
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    @NotNull(message = "Account type is required")
    private AccountType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AccountStatus status;

    @Column(name = "min_balance")
    private BigDecimal minBalance;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();

        // Set default status if not provided
        if (status == null) {
            status = AccountStatus.ACTIVE;
        }

        // Set default balance if not provided
        if (balance == null) {
            balance = BigDecimal.ZERO;
        }

        // Set minimum balance based on account type
        if (type == AccountType.INVESTMENT) {
            minBalance = new BigDecimal("10000.00");
        } else {
            minBalance = BigDecimal.ZERO;
        }

        // Validate investment account minimum balance
        if (type == AccountType.INVESTMENT && balance.compareTo(minBalance) < 0) {
            throw new IllegalArgumentException("Investment accounts require minimum balance of 10,000");
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
}