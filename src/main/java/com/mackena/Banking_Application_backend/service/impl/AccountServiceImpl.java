package com.mackena.Banking_Application_backend.service.impl;

import com.mackena.Banking_Application_backend.dto.response.LowBalanceAccountsResponse;
import com.mackena.Banking_Application_backend.dtos.response.AccountListResponse;
import com.mackena.Banking_Application_backend.dtos.response.AccountResponse;
import com.mackena.Banking_Application_backend.dtos.response.TotalBalanceResponse;
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
    public AccountResponse getAccountByNumber(String accountNumber, Long CurrentUserId, boolean isAdmin) {
        return null;
    }

    @Override
    public LowBalanceAccountsResponse getLowBalanceAccounts(String accountNumber, Long CurrentUserId, boolean isAdmin) {
        return null;
    }

    @Override
    public TotalBalanceResponse getTotalBalance(String accountNumber, Long CurrentUserId, boolean isAdmin) {
        return null;
    }

    @Override
    public Account findAccountByNumber(String accountNumber) {
        return null;
    }

    @Override
    public void validateAccountOwnership(Account account, Long userId) {

    }
}
