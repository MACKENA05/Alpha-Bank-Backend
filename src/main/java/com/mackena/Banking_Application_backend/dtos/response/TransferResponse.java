package com.mackena.Banking_Application_backend.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse implements Serializable {

    private String transferReference;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private BigDecimal amount;
    private String notes;
    private String status;
    private BigDecimal senderBalanceAfter;
    private LocalDateTime transactionDate;
    private String message;
}