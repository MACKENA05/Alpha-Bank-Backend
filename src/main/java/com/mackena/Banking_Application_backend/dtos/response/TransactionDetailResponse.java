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
public class TransactionDetailResponse {

    private Long id;
    private String referenceNumber;
    private String transferReference;
    private BigDecimal amount;
    private String transactionType;
    private String transactionDirection;
    private String description;
    private String status;
    private BigDecimal balanceAfter;
    private LocalDateTime createdAt;
    private String accountNumber;
}