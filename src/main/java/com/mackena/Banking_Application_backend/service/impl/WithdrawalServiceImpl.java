package com.mackena.Banking_Application_backend.service.impl;

import com.mackena.Banking_Application_backend.dtos.request.WithdrawalRequest;
import com.mackena.Banking_Application_backend.dtos.response.TransactionResponse;
import com.mackena.Banking_Application_backend.exceptions.InvalidPinException;
import com.mackena.Banking_Application_backend.exceptions.InsufficientFundsException;
import com.mackena.Banking_Application_backend.models.entity.Account;
import com.mackena.Banking_Application_backend.models.entity.Transaction;
import com.mackena.Banking_Application_backend.models.entity.User;
import com.mackena.Banking_Application_backend.models.enums.TransactionDirection;
import com.mackena.Banking_Application_backend.models.enums.TransactionStatus;
import com.mackena.Banking_Application_backend.models.enums.TransactionType;
import com.mackena.Banking_Application_backend.repository.AccountRepository;
import com.mackena.Banking_Application_backend.repository.TransactionRepository;
import com.mackena.Banking_Application_backend.service.WithdrawalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WithdrawalServiceImpl implements WithdrawalService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public TransactionResponse processWithdrawal(WithdrawalRequest request, User currentUser) {

        // Validate and get account
        Account account = validateWithdrawalAccount(request.getAccountNumber(), currentUser);

        //Validate PIN
        validateTransactionPin(request.getTransactionPin(), account);

        // validates if balance is sufficient
        validateSufficientBalance(account, request.getAmount());

        try {
            // Creates withdrawal transaction
            Transaction transaction = createWithdrawalTransaction(account, request);

            // Updates account balance
            account.setBalance(account.getBalance().subtract(request.getAmount()));
            transaction.setBalanceAfter(account.getBalance());

            // Save transaction and account
            transactionRepository.save(transaction);
            accountRepository.save(account);

            log.info("Withdrawal completed successfully. Reference: {}", transaction.getReferenceNumber());

            return TransactionResponse.builder()
                    .referenceNumber(transaction.getReferenceNumber())
                    .accountNumber(request.getAccountNumber())
                    .amount(request.getAmount())
                    .transactionType("WITHDRAWAL")
                    .notes(request.getNotes())
                    .status("COMPLETED")
                    .balanceAfter(account.getBalance())
                    .transactionDate(LocalDateTime.now())
                    .message("Withdrawal completed successfully")
                    .build();

        } catch (Exception e) {
            log.error("Withdrawal failed for account: {}, Error: {}", request.getAccountNumber(), e.getMessage());
            throw new RuntimeException("Withdrawal failed: " + e.getMessage());
        }
    }

    private Account validateWithdrawalAccount(String accountNumber, User currentUser) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new InsufficientFundsException("Account not found"));

        // Only account owner can withdraw (admin cannot withdraw )
        if (!account.getUser().getId().equals(currentUser.getId())) {
            throw new InsufficientFundsException("You can only withdraw from your own account");
        }

        if (!account.isActive()) {
            throw new InsufficientFundsException("Account is not active");
        }

        return account;
    }

    private void validateTransactionPin(String enteredPin, Account account) {
        if (account.getTransactionPin() == null) {
            throw new InvalidPinException("Transaction PIN not set for this account");
        }

        if (!passwordEncoder.matches(enteredPin, account.getTransactionPin())) {
            throw new InvalidPinException("Invalid transaction PIN");
        }
    }

    private void validateSufficientBalance(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient balance");
        }

        // Optional: Add minimum balance check
        BigDecimal minBalance = BigDecimal.valueOf(10.00);
        if (account.getBalance().subtract(amount).compareTo(minBalance) < 0) {
            throw new InsufficientFundsException("Cannot withdraw. Minimum balance of Kes10 must be maintained");
        }
    }

    private Transaction createWithdrawalTransaction(Account account, WithdrawalRequest request) {
        return Transaction.builder()
                .account(account)
                .amount(request.getAmount())
                .transactionType(TransactionType.WITHDRAW)
                .transactionDirection(TransactionDirection.DEBIT)
                .description("ATM Withdrawal - " + (request.getNotes() != null ? request.getNotes() : "Cash withdrawal"))
                .referenceNumber(generateReferenceNumber())
                .status(TransactionStatus.COMPLETED)
                .build();
    }

    private String generateReferenceNumber() {
        return "WTH" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}