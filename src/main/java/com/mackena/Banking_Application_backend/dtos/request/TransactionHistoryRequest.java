package com.mackena.Banking_Application_backend.dtos.request;


import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionHistoryRequest {

    private String accountNumber;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;

    private String transactionType; // DEPOSIT, WITHDRAW, TRANSFER_IN, TRANSFER_OUT
    private String transactionDirection; // DEBIT, CREDIT

    private BigDecimal minAmount;
    private BigDecimal maxAmount;

    private String sortBy = "createdAt"; // createdAt, amount, transactionType
    private String sortDirection = "DESC"; // ASC, DESC

    private int page = 0;
    private int size = 20;
}
