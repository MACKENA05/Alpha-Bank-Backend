package com.mackena.Banking_Application_backend.dtos.request;

import com.mackena.Banking_Application_backend.models.enums.AccountType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    @Pattern(regexp = "\\d{4}", message = "Transaction PIN must be exactly 4 digits")
    @NotBlank(message = "Transaction PIN is required")
    private String transactionPin;

    @Pattern(regexp = "\\d{4}", message = "Confirm PIN must be exactly 4 digits")
    @NotBlank(message = "Confirm PIN is required")
    private String confirmPin;

    @DecimalMin(value = "0.0", inclusive = false, message = "Initial deposit must be greater than 0")
    private BigDecimal initialDeposit;
}