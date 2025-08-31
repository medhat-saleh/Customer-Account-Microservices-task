package com.BlackstoneeIT.account_management_service.dto;

import com.BlackstoneeIT.account_management_service.entity.Account;
import com.BlackstoneeIT.account_management_service.enums.AccountStatus;
import com.BlackstoneeIT.account_management_service.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {
    private Long id;
    private Long customerId;
    private BigDecimal balance;
    private AccountType type;
    private AccountStatus status;
    private BigDecimal minBalance;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public static AccountResponse fromEntity(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getCustomerId(),
                account.getBalance(),
                account.getType(),
                account.getStatus(),
                account.getMinBalance(),
                account.getCreatedDate(),
                account.getUpdatedDate()
        );
    }
}