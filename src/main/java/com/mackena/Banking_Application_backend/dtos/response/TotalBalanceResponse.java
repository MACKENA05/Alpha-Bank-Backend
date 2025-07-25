package com.mackena.Banking_Application_backend.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalBalanceResponse {
    private BigDecimal totalSystemBalance;
    private int totalActiveAccounts;
    private int totalUser;
    private BigDecimal averageBalancePerAccount;
    private String Message;
}
