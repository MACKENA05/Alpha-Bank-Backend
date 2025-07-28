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
public class CreateAccountResponse {

    private boolean success;
    private String message;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal balance;
    private boolean isActive;
    private LocalDateTime createdAt;

    public static CreateAccountResponse success(String accountNumber, AccountType accountType,
                                                BigDecimal balance, LocalDateTime createdAt) {
        return CreateAccountResponse.builder()
                .success(true)
                .message("Account created successfully")
                .accountNumber(accountNumber)
                .accountType(accountType)
                .balance(balance)
                .isActive(true)
                .createdAt(createdAt)
                .build();
    }

    public static CreateAccountResponse failure(String message) {
        return CreateAccountResponse.builder()
                .success(false)
                .message(message)
                .isActive(false)
                .balance(BigDecimal.ZERO)
                .build();
    }
}