package com.mackena.Banking_Application_backend.dtos.response;

import com.mackena.Banking_Application_backend.models.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private Long id;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal balance;
    private String formattedBalance;
    private boolean isActive;
    private boolean lowBalance;
    private UserResponse user;
    private LocalDateTime createdAt;
}