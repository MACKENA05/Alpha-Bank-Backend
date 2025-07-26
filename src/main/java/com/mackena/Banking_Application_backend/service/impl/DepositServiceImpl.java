package com.mackena.Banking_Application_backend.service.impl;

import com.mackena.Banking_Application_backend.dtos.request.DepositRequest;
import com.mackena.Banking_Application_backend.dtos.response.TransactionResponse;
import com.mackena.Banking_Application_backend.exceptions.InsufficientFundsException;
import com.mackena.Banking_Application_backend.models.entity.Account;
import com.mackena.Banking_Application_backend.models.entity.Transaction;
import com.mackena.Banking_Application_backend.models.entity.User;
import com.mackena.Banking_Application_backend.models.enums.TransactionDirection;
import com.mackena.Banking_Application_backend.models.enums.TransactionStatus;
import com.mackena.Banking_Application_backend.models.enums.TransactionType;
import com.mackena.Banking_Application_backend.repository.AccountRepository;
import com.mackena.Banking_Application_backend.repository.TransactionRepository;
import com.mackena.Banking_Application_backend.service.DepositService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepositServiceImpl implements DepositService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    @Override
    public TransactionResponse processDeposit(DepositRequest request, User adminUser) {

        //Validate account exists
        Account account = validateDepositAccount(request.getAccountNumber());

        try {
            //Create deposit transaction
            Transaction transaction = createDepositTransaction(account, request, adminUser);

            //Update account balance
            account.setBalance(account.getBalance().add(request.getAmount()));
            transaction.setBalanceAfter(account.getBalance());

            //Save transaction and account
            transactionRepository.save(transaction);
            accountRepository.save(account);

            log.info("Deposit completed successfully by admin: {}. Reference: {}",
                    adminUser.getEmail(), transaction.getReferenceNumber());

            return TransactionResponse.builder()
                    .referenceNumber(transaction.getReferenceNumber())
                    .accountNumber(request.getAccountNumber())
                    .amount(request.getAmount())
                    .transactionType("DEPOSIT")
                    .notes(request.getNotes())
                    .status("COMPLETED")
                    .balanceAfter(account.getBalance())
                    .transactionDate(LocalDateTime.now())
                    .message("Deposit completed successfully")
                    .depositSource(request.getDepositSource())
                    .build();

        } catch (Exception e) {
            log.error("Deposit failed for account: {}, Error: {}", request.getAccountNumber(), e.getMessage());
            throw new RuntimeException("Deposit failed: " + e.getMessage());
        }
    }

    private Account validateDepositAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new InsufficientFundsException("Account not found"));

        if (!account.isActive()) {
            throw new InsufficientFundsException("Cannot deposit to inactive account");
        }

        return account;
    }

    private Transaction createDepositTransaction(Account account, DepositRequest request, User adminUser) {
        String description = String.format("Deposit (%s) by Admin - %s",
                request.getDepositSource(),
                request.getNotes() != null ? request.getNotes() : "Cash deposit");

        return Transaction.builder()
                .account(account)
                .amount(request.getAmount())
                .transactionType(TransactionType.DEPOSIT)
                .transactionDirection(TransactionDirection.CREDIT)
                .description(description)
                .referenceNumber(generateReferenceNumber())
                .status(TransactionStatus.COMPLETED)
                .build();
    }

    private String generateReferenceNumber() {
        return "DEP" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
