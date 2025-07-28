package com.mackena.Banking_Application_backend.service.impl;

import com.mackena.Banking_Application_backend.dtos.request.TransferRequest;
import com.mackena.Banking_Application_backend.dtos.response.TransferResponse;
import com.mackena.Banking_Application_backend.exceptions.InsufficientFundsException;
import com.mackena.Banking_Application_backend.exceptions.InvalidAccountException;
import com.mackena.Banking_Application_backend.exceptions.InvalidPinException;
import com.mackena.Banking_Application_backend.models.entity.Account;
import com.mackena.Banking_Application_backend.models.entity.Transaction;
import com.mackena.Banking_Application_backend.models.entity.User;
import com.mackena.Banking_Application_backend.models.enums.TransactionDirection;
import com.mackena.Banking_Application_backend.models.enums.TransactionStatus;
import com.mackena.Banking_Application_backend.models.enums.TransactionType;
import com.mackena.Banking_Application_backend.repository.AccountRepository;
import com.mackena.Banking_Application_backend.repository.TransactionRepository;
import com.mackena.Banking_Application_backend.service.TransferService;
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
public class TransferServiceImpl implements TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransferResponse transferMoney(TransferRequest request, User currentUser)
            throws InvalidAccountException {

        log.info("Processing transfer for user: {} amount: {}",
                currentUser.getEmail(), request.getAmount());


        // Pre-validate all inputs before starting transaction
        validateTransferRequest(request);

        // 1. Validate accounts - with explicit null checks
        Account senderAccount = validateSenderAccount(request.getSenderAccountNumber(), currentUser);
        Account receiverAccount = validateReceiverAccount(request.getReceiverAccountNumber());

        // 2. Validate PIN
        validateTransactionPin(request.getTransactionPin(), senderAccount);

        // 3. Validate sufficient balance
        validateSufficientBalance(senderAccount, request.getAmount());

        // 4. Generate transfer reference
        String debitReferenceNumber = generateReferenceNumber();
        String creditReferenceNumber = generateReferenceNumber();

        // 5. Create debit transaction (sender)
        Transaction debitTransaction = createDebitTransaction(
                senderAccount, request.getAmount(), request.getNotes(), creditReferenceNumber);

        // 6. Create credit transaction (receiver)
        Transaction creditTransaction = createCreditTransaction(
                receiverAccount, request.getAmount(), request.getNotes(), debitReferenceNumber);

        // 7. Update balances
        updateAccountBalance(senderAccount, request.getAmount().negate());
        updateAccountBalance(receiverAccount, request.getAmount());

        // 8. Set balance after for transactions
        debitTransaction.setBalanceAfter(senderAccount.getBalance());
        creditTransaction.setBalanceAfter(receiverAccount.getBalance());

        // 9. Save transactions
        transactionRepository.save(debitTransaction);
        transactionRepository.save(creditTransaction);

        // 10. Save updated accounts
        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        log.info("Transfer completed successfully. Reference: {}", debitReferenceNumber);

        return TransferResponse.builder()
                .transferReference(debitReferenceNumber)
                .senderAccountNumber(request.getSenderAccountNumber())
                .receiverAccountNumber(request.getReceiverAccountNumber())
                .amount(request.getAmount())
                .notes(request.getNotes())
                .status("COMPLETED")
                .senderBalanceAfter(senderAccount.getBalance())
                .transactionDate(LocalDateTime.now())
                .message("Transfer completed successfully")
                .build();
    }

    private void validateTransferRequest(TransferRequest request) throws InvalidAccountException {
        if (request == null) {
            throw new InvalidAccountException("Transfer request cannot be null");
        }

        if (request.getSenderAccountNumber() == null || request.getSenderAccountNumber().trim().isEmpty()) {
            throw new InvalidAccountException("Sender account number is required");
        }

        if (request.getReceiverAccountNumber() == null || request.getReceiverAccountNumber().trim().isEmpty()) {
            throw new InvalidAccountException("Receiver account number is required");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAccountException("Amount must be greater than zero");
        }

        if (request.getTransactionPin() == null || request.getTransactionPin().trim().isEmpty()) {
            throw new InvalidPinException("Transaction PIN is required");
        }

        // Check if sender and receiver are the same
        if (request.getSenderAccountNumber().trim().equalsIgnoreCase(request.getReceiverAccountNumber().trim())) {
            throw new InvalidAccountException("Cannot transfer to the same account");
        }
    }

    private Account validateSenderAccount(String accountNumber, User currentUser) throws InvalidAccountException {
        log.debug("Validating sender account: {}", accountNumber);

        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new InvalidAccountException("Sender account number is required");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber.trim())
                .orElseThrow(() -> new InvalidAccountException("Sender account not found: " + accountNumber));

        // Check ownership or admin privilege
        if (!account.getUser().getId().equals(currentUser.getId()) && !currentUser.getRole().isAdmin()) {
            throw new InvalidAccountException("You don't have permission to transfer from this account");
        }

        if (!account.isActive()) {
            throw new InvalidAccountException("Sender account is not active");
        }

        log.debug("Sender account validation successful");
        return account;
    }

    private Account validateReceiverAccount(String accountNumber) throws InvalidAccountException {
        log.debug("Validating receiver account: {}", accountNumber);

        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new InvalidAccountException("Receiver account number is required");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber.trim())
                .orElseThrow(() -> new InvalidAccountException("Receiver account not found: " + accountNumber));

        if (!account.isActive()) {
            throw new InvalidAccountException("Receiver account is not active");
        }

        log.debug("Receiver account validation successful");
        return account;
    }

    private void validateTransactionPin(String enteredPin, Account account) throws InvalidPinException {
        log.debug("Validating transaction PIN for account: {}", account.getAccountNumber());

        if (enteredPin == null || enteredPin.trim().isEmpty()) {
            throw new InvalidPinException("Transaction PIN is required");
        }

        if (account.getTransactionPin() == null) {
            throw new InvalidPinException("Transaction PIN not set for this account");
        }

        if (!passwordEncoder.matches(enteredPin, account.getTransactionPin())) {
            throw new InvalidPinException("Invalid transaction PIN");
        }

        log.debug("Transaction PIN validation successful");
    }

    private void validateSufficientBalance(Account account, BigDecimal amount) throws InsufficientFundsException {
        log.debug("Validating sufficient balance. Account balance: {}, Transfer amount: {}",
                account.getBalance(), amount);

        if (account.getBalance() == null) {
            throw new InsufficientFundsException("Account balance is not available");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient balance in sender account. Available: "
                    + account.getBalance() + ", Required: " + amount);
        }

        log.debug("Balance validation successful");
    }

    private Transaction createDebitTransaction(Account account, BigDecimal amount,
                                               String notes, String transferReference) {
        return Transaction.builder()
                .account(account)
                .amount(amount)
                .transactionType(TransactionType.TRANSFER)
                .transactionDirection(TransactionDirection.DEBIT)
                .description("Transfer to " + (notes != null ? notes : "N/A"))
                .referenceNumber(generateReferenceNumber())
                .transferReference(transferReference)
                .status(TransactionStatus.COMPLETED)
                .build();
    }

    private Transaction createCreditTransaction(Account account, BigDecimal amount,
                                                String notes, String transferReference) {
        return Transaction.builder()
                .account(account)
                .amount(amount)
                .transactionType(TransactionType.TRANSFER)
                .transactionDirection(TransactionDirection.CREDIT)
                .description("Transfer from " + (notes != null ? notes : "N/A"))
                .referenceNumber(generateReferenceNumber())
                .transferReference(transferReference)
                .status(TransactionStatus.COMPLETED)
                .build();
    }

    private void updateAccountBalance(Account account, BigDecimal amount) {
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }

        BigDecimal newBalance = account.getBalance().add(amount);
        log.debug("Updating account {} balance from {} to {}",
                account.getAccountNumber(), account.getBalance(), newBalance);
        account.setBalance(newBalance);
    }

    private String generateReferenceNumber() {
        return "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}