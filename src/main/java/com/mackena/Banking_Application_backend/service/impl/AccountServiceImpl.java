package com.mackena.Banking_Application_backend.service.impl;

import com.mackena.Banking_Application_backend.dto.response.LowBalanceAccountsResponse;
import com.mackena.Banking_Application_backend.dtos.request.CreateAccountRequest;
import com.mackena.Banking_Application_backend.dtos.response.AccountListResponse;
import com.mackena.Banking_Application_backend.dtos.response.AccountResponse;
import com.mackena.Banking_Application_backend.dtos.response.CreateAccountResponse;
import com.mackena.Banking_Application_backend.dtos.response.TotalBalanceResponse;
import com.mackena.Banking_Application_backend.exceptions.AccountAccessDeniedException;
import com.mackena.Banking_Application_backend.exceptions.AccountNotFoundException;
import com.mackena.Banking_Application_backend.exceptions.UserNotFoundException;
import com.mackena.Banking_Application_backend.models.entity.Account;
import com.mackena.Banking_Application_backend.models.entity.User;
import com.mackena.Banking_Application_backend.repository.AccountRepository;
import com.mackena.Banking_Application_backend.repository.UserRepository;
import com.mackena.Banking_Application_backend.service.AccountService;
import com.mackena.Banking_Application_backend.util.converter.EntityConverter;
import com.mackena.Banking_Application_backend.util.generator.AccountNumberGenerator;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final EntityConverter accountConverter;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountNumberGenerator accountNumberGenerator;


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
    public AccountResponse getAccountByNumber(String accountNumber, Long currentUserId, boolean isAdmin) {

        Account account = findAccountByNumber(accountNumber);

        if(!isAdmin) {
            validateAccountOwnership(account, currentUserId);
        }
        return accountConverter.toAccountResponse(account);
    }

    @Override
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
    public CreateAccountResponse createAccountForUser(CreateAccountRequest request, String userEmail) {
        log.info("Creating new account for user: {}", userEmail);

        // Validate PIN confirmation
        if (!request.getTransactionPin().equals(request.getConfirmPin())) {
            throw new IllegalArgumentException("Transaction PIN and confirmation PIN do not match");
        }

        // Find the user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + userEmail));

        // Check if user already has this account type (optional - remove if you want multiple of same type)
        boolean hasAccountType = user.getAccounts().stream()
                .anyMatch(account -> account.getAccountType() == request.getAccountType() && account.isActive());

        if (hasAccountType) {
            throw new IllegalArgumentException("User already has an active " +
                    request.getAccountType().name().toLowerCase() + " account");
        }

        // Log current account count
        int currentAccountCount = user.getAccounts().size();
        log.info("User {} currently has {} accounts, creating account #{}",
                userEmail, currentAccountCount, currentAccountCount + 1);

        // Generate unique account number using AuthService
        String accountNumber = generateUniqueAccountNumber();

        // Create new account
        Account newAccount = Account.builder()
                .user(user)
                .accountNumber(accountNumber)
                .accountType(request.getAccountType())
                .balance(request.getInitialDeposit() != null ? request.getInitialDeposit() : BigDecimal.ZERO)
                .transactionPin(passwordEncoder.encode(request.getTransactionPin()))
                .isActive(true)
                .build();

        // Save account
        Account savedAccount = accountRepository.save(newAccount);

        log.info("Successfully created account {} for user {}", accountNumber, userEmail);

        return CreateAccountResponse.success(
                savedAccount.getAccountNumber(),
                savedAccount.getAccountType(),
                savedAccount.getBalance(),
                savedAccount.getCreatedAt()
        );
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            accountNumber = accountNumberGenerator.generateAccountNumber(); // Use the same generator as AuthService
        } while (accountRepository.existsByAccountNumber(accountNumber)); // More efficient than checking all users

        return accountNumber;
    }



    @Override
    public TotalBalanceResponse getTotalSystemBalance() {
        BigDecimal totalBalance = accountRepository.getTotalSystemBalance();
        long totalActiveAccount = accountRepository.countActiveAccounts();
        long totalUsers = accountRepository.countUsersWithActiveAccounts();
        BigDecimal averageBalance = BigDecimal.ZERO;

        if (totalActiveAccount > 0) {
            averageBalance = totalBalance.divide(BigDecimal.valueOf(totalActiveAccount));
        }

        return TotalBalanceResponse.builder()
                .totalSystemBalance(totalBalance)
                .totalActiveAccounts((int) totalActiveAccount)
                .totalUser((int) totalUsers)
                .averageBalancePerAccount(averageBalance)
                .message("Total System Balance: " + totalBalance)
                .build();
    }


    @Override
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

    @Override
    public List<Account> getAccountsByUser(User user) {
        log.debug("Getting accounts for user: {}", user.getEmail());
        return accountRepository.findByUserAndIsActiveTrue(user);
    }

    @Override
    public Account findByAccountNumber(String accountNumber) {
        log.debug("Finding account by number: {}", accountNumber);
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
    }

    @Override
    public List<Account> findAccountsWithBalanceBelow(BigDecimal threshold) {
        log.debug("Finding accounts with balance below: {}", threshold);
        return accountRepository.findByBalanceLessThanAndIsActiveTrue(threshold);
    }


    @Override
    public long getTotalAccountCount() {
        log.debug("Getting total account count");
        return accountRepository.count();
    }

    @Override
    public long getActiveAccountCount() {
        log.debug("Getting active account count");
        return accountRepository.countByIsActiveTrue();
    }

    @Override
    public List<Account> searchAccounts(String query, int page, int size) {
        log.debug("Searching accounts with query: {}, page: {}, size: {}", query, page, size);

        Pageable pageable = PageRequest.of(page, size);

        // Search by account number, user first name, last name, or email
        return accountRepository.findByAccountNumberContainingIgnoreCaseOrUser_FirstNameContainingIgnoreCaseOrUser_LastNameContainingIgnoreCaseOrUser_EmailContainingIgnoreCase(
                query, query, query, query, pageable);
    }
}
