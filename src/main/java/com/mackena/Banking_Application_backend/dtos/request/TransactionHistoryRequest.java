package com.mackena.Banking_Application_backend.dtos.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionHistoryRequest {

    private String accountNumber;

    private Long userId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;;

    private String transactionType; // DEPOSIT, WITHDRAW, TRANSFER_IN, TRANSFER_OUT
    private String transactionDirection; // DEBIT, CREDIT

    private BigDecimal minAmount;
    private BigDecimal maxAmount;

    private String sortBy = "createdAt"; // createdAt, amount, transactionType
    private String sortDirection = "DESC"; // ASC, DESC

    private int page = 0;
    private int size = 20;



}
