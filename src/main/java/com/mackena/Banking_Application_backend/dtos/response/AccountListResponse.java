package com.mackena.Banking_Application_backend.dtos.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountListResponse {
    private List<AccountResponse> accounts;
    private int totalAccounts;
    private BigDecimal totalBalance;
    private String message;
}
