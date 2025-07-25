package com.mackena.Banking_Application_backend.service.impl;

import com.mackena.Banking_Application_backend.dto.response.LowBalanceAccountsResponse;
import com.mackena.Banking_Application_backend.dtos.response.AccountListResponse;
import com.mackena.Banking_Application_backend.dtos.response.AccountResponse;
import com.mackena.Banking_Application_backend.dtos.response.TotalBalanceResponse;
import com.mackena.Banking_Application_backend.exceptions.AccountAccessDeniedException;
import com.mackena.Banking_Application_backend.exceptions.AccountNotFoundException;
import com.mackena.Banking_Application_backend.models.entity.Account;
import com.mackena.Banking_Application_backend.repository.AccountRepository;
import com.mackena.Banking_Application_backend.service.AccountService;
import com.mackena.Banking_Application_backend.util.converter.EntityConverter;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final EntityConverter accountConverter;

    @Transactional(readOnly = true)
    @Override
    public AccountListResponse getUserAccounts(Long userId) {
        List<Account> accounts = accountRepository.findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(userId);

        List<AccountResponse> accountResponses = accounts.stream()
                .map(accountConverter::toAccountResponse)
                .collect(Collectors.toList());

        BigDecimal totalBalance = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return AccountListResponse.builder()
                .accounts(accountResponses)
                .totalAccounts(accounts.size())
                .totalBalance(totalBalance)
                .message("Total balance: " + totalBalance)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccountByNumber(String accountNumber, Long currentUserId, boolean isAdmin) {

        Account account = findAccountByNumber(accountNumber);

        if(!isAdmin) {
            validateAccountOwnership(account, currentUserId);
        }
        return accountConverter.toAccountResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public LowBalanceAccountsResponse getLowBalanceAccounts(BigDecimal threshold) {
        List<Account> lowBalanceAccounts = accountRepository.findAccountsWithLowBalance(threshold);

        List<AccountResponse> accountResponses = lowBalanceAccounts.stream()
                .map(accountConverter::toAccountResponse)
                .collect(Collectors.toList());

        BigDecimal totalLowBalance = lowBalanceAccounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return LowBalanceAccountsResponse.builder()
                .accounts(accountResponses)
                .totalLowBalanceAccounts(lowBalanceAccounts.size())
                .threshold(threshold)
                .totalLowBalance(totalLowBalance)
                .message("Total low balance: " + totalLowBalance)
                .build();

    }

    @Override
    @Transactional(readOnly = true)
    public TotalBalanceResponse getTotalSystemBalance() {

        BigDecimal totalBalance = accountRepository.getTotalSystemBalance();
        long totalActiveAccount = accountRepository.countActiveAccounts();
        long totalUsers = accountRepository.countUsersWithActiveAccounts();
        BigDecimal averageBalance = BigDecimal.ZERO;

        if(totalActiveAccount > 0) {
            averageBalance = totalBalance.divide(BigDecimal.valueOf(totalActiveAccount));
        }

        return TotalBalanceResponse.builder()
                .totalSystemBalance(totalBalance)
                .totalActiveAccounts((int)totalActiveAccount)
                .totalUser((int) totalUsers)
                .averageBalancePerAccount(averageBalance)
                .message("Total System Balance: " + totalBalance)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Account findAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));

    }

    @Override
    public void validateAccountOwnership(Account account, Long userId) {
        if(!account.getUser().getId().equals(userId)){
            throw new AccountAccessDeniedException("You dont have permission to access this account");
        }

    }
}
