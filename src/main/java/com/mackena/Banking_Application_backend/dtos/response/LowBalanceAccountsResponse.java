package com.mackena.Banking_Application_backend.dto.response;

import com.mackena.Banking_Application_backend.dtos.response.AccountResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LowBalanceAccountsResponse {
    private List<AccountResponse> accounts;
    private int totalLowBalanceAccounts;
    private BigDecimal threshold;
    private BigDecimal totalLowBalance;
    private String message;
}