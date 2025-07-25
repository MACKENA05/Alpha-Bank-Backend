package com.mackena.Banking_Application_backend.service;

import com.mackena.Banking_Application_backend.dtos.response.AccountListResponse;
import com.mackena.Banking_Application_backend.dtos.response.AccountResponse;
import com.mackena.Banking_Application_backend.dtos.response.TotalBalanceResponse;
import com.mackena.Banking_Application_backend.models.entity.Account;
import com.mackena.Banking_Application_backend.service.impl.AccountServiceImpl;
import com.mackena.Banking_Application_backend.dto.response.LowBalanceAccountsResponse;

import java.math.BigDecimal;

public interface AccountService{
    AccountListResponse getUserAccounts(Long userId);
    AccountResponse getAccountByNumber(String accountNumber, Long CurrentUserId, boolean isAdmin);
    LowBalanceAccountsResponse getLowBalanceAccounts(BigDecimal threshold);
    TotalBalanceResponse getTotalSystemBalance();
    Account findAccountByNumber(String accountNumber);
    void validateAccountOwnership(Account account, Long userId);

}
