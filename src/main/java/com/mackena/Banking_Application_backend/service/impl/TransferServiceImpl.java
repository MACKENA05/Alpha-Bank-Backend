package com.mackena.Banking_Application_backend.service;

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
import com.mackena.Banking_Application_backend.exceptions.InvalidAccountException;
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

    @Transactional
    @Override
    public TransferResponse transferMoney(TransferRequest request, User currentUser) {

        // 1. Validate accounts
        Account senderAccount = null;
        try {
            senderAccount = validateSenderAccount(request.getSenderAccountNumber(), currentUser);
        } catch (InvalidAccountException e) {
            throw new RuntimeException(e);
        }
        Account receiverAccount = null;
        try {
            receiverAccount = validateReceiverAccount(request.getReceiverAccountNumber());
        } catch (InvalidAccountException e) {
            throw new RuntimeException(e);
        }

        // 2. Validate PIN
        validateTransactionPin(request.getTransactionPin(), senderAccount);

        // 3. Validate sufficient balance
        validateSufficientBalance(senderAccount, request.getAmount());

        // 4. Generate transfer reference
        String debitReferenceNumber = generateReferenceNumber();
        String creditReferenceNumber = generateReferenceNumber();

        try {
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

        } catch (Exception e) {
            throw new RuntimeException("Transfer failed: " + e.getMessage());
        }
    }

    private Account validateSenderAccount(String accountNumber, User currentUser) throws InvalidAccountException {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new InsufficientFundsException("Sender account not found"));

        if (!account.getUser().getId().equals(currentUser.getId()) && !currentUser.getRole().isAdmin()) {
            throw new InsufficientFundsException("You don't have permission to transfer from this account");
        }

        if (!account.isActive()) {
            throw  new InvalidAccountException("Sender account is not active");
        }

        return account;
    }

    private Account validateReceiverAccount(String accountNumber) throws InvalidAccountException {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new InvalidAccountException("Receiver account not found"));

        if (!account.isActive()) {
            throw new InvalidAccountException("Receiver account is not active");
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
            throw new InsufficientFundsException("Insufficient balance in sender account");
        }
    }

    private String generateTransferReference() {
        return "TXF" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }

    private Transaction createDebitTransaction(Account account, BigDecimal amount,
                                               String notes, String transferReference) {
        return Transaction.builder()
                .account(account)
                .amount(amount)
                .transactionType(TransactionType.TRANSFER)
                .transactionDirection(TransactionDirection.DEBIT)
                .description("Transfer to " + notes)
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
                .description("Transfer from " + notes)
                .referenceNumber(generateReferenceNumber())
                .transferReference(transferReference)
                .status(TransactionStatus.COMPLETED)
                .build();
    }

    private void updateAccountBalance(Account account, BigDecimal amount) {
        account.setBalance(account.getBalance().add(amount));
    }

    private String generateReferenceNumber() {
        return "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}