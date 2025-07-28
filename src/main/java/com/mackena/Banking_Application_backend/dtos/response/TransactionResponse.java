package com.mackena.Banking_Application_backend.dtos.response;

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
public class TransactionResponse {

    private String referenceNumber;
    private String accountNumber;
    private BigDecimal amount;
    private String transactionType;
    private String notes;
    private String status;
    private BigDecimal balanceAfter;
    private LocalDateTime transactionDate;
    private String message;
    private String depositSource; // For deposits only
}