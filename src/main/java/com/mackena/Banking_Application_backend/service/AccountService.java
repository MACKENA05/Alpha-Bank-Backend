package com.mackena.Banking_Application_backend.service;

import com.mackena.Banking_Application_backend.dtos.request.CreateAccountRequest;
import com.mackena.Banking_Application_backend.dtos.response.AccountListResponse;
import com.mackena.Banking_Application_backend.dtos.response.AccountResponse;
import com.mackena.Banking_Application_backend.dtos.response.CreateAccountResponse;
import com.mackena.Banking_Application_backend.dtos.response.TotalBalanceResponse;
import com.mackena.Banking_Application_backend.models.entity.Account;
import com.mackena.Banking_Application_backend.models.entity.User;
import com.mackena.Banking_Application_backend.dto.response.LowBalanceAccountsResponse;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService{

    @Transactional(readOnly = true)
    AccountListResponse getUserAccounts(Long userId);

    @Transactional(readOnly = true)
    AccountResponse getAccountByNumber(String accountNumber, Long CurrentUserId, boolean isAdmin);

    @Transactional(readOnly = true)
    LowBalanceAccountsResponse getLowBalanceAccounts(BigDecimal threshold);

    CreateAccountResponse createAccountForUser(CreateAccountRequest request, String userEmail);


    List<Account> getAccountsByUser(User user);

    Account findByAccountNumber(String accountNumber);

    List<Account> findAccountsWithBalanceBelow(BigDecimal threshold);

    TotalBalanceResponse getTotalSystemBalance();

    @Transactional(readOnly = true)
    Account findAccountByNumber(String accountNumber);

    @Transactional
    void validateAccountOwnership(Account account, Long userId);

    long getTotalAccountCount();

    long getActiveAccountCount();

    List<Account> searchAccounts(String query, int page, int size);


}
