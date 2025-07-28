package com.mackena.Banking_Application_backend.controller;

import com.mackena.Banking_Application_backend.dtos.request.*;
import com.mackena.Banking_Application_backend.dtos.response.*;
import com.mackena.Banking_Application_backend.exceptions.InvalidAccountException;
import com.mackena.Banking_Application_backend.models.entity.User;
import com.mackena.Banking_Application_backend.security.CurrentUser;
import com.mackena.Banking_Application_backend.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final WithdrawalService withdrawalService;
    private final DepositService depositService;
    private final TransferService transferService;
    private final TransactionHistoryService transactionHistoryService;
    private final UserService userService;

    // Withdrawal endpoint (users with account only can withdraw)
    @PostMapping("/withdraw")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TransactionResponse> withdrawMoney(
            @Valid @RequestBody WithdrawalRequest request,
            @CurrentUser UserDetails userDetails) {

        log.info("Withdrawal request from user: {} for amount: {}",
                userDetails.getUsername(), request.getAmount());

        User currentUser = userService.findUserByEmail(userDetails.getUsername());
        TransactionResponse response = withdrawalService.processWithdrawal(request, currentUser);

        return ResponseEntity.ok(response);
    }

    // Deposit endpoint(only admins can deposit)
    @PostMapping("/deposit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionResponse> depositMoney(
            @Valid @RequestBody DepositRequest request,
            @CurrentUser UserDetails userDetails) {

        log.info("Deposit request from admin: {} for amount: {} to account: {}",
                userDetails.getUsername(), request.getAmount(), request.getAccountNumber());

        User adminUser = userService.findUserByEmail(userDetails.getUsername());
        TransactionResponse response = depositService.processDeposit(request, adminUser);

        return ResponseEntity.ok(response);
    }

    // Transfer endpoint(Users can transfer from their own accounts)
    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TransferResponse> transferMoney(
            @Valid @RequestBody TransferRequest request,
            @CurrentUser UserDetails userDetails) throws InvalidAccountException {

        log.info("Transfer request received from user: {} for amount: {}",
                userDetails.getUsername(), request.getAmount());

            User currentUser = userService.findUserByEmail(userDetails.getUsername());
            TransferResponse response = transferService.transferMoney(request, currentUser);

            log.info("Transfer completed successfully with reference: {}", response.getTransferReference());
            return ResponseEntity.ok(response);
    }

    // Getting transaction history for specific user
    @GetMapping("/history")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransactionHistoryResponse> getTransactionHistory(
            @RequestParam(required = false) String accountNumber,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) String transactionDirection,
            @RequestParam(required = false) String minAmount,
            @RequestParam(required = false) String maxAmount,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @CurrentUser UserDetails userDetails) {

        log.info("Transaction history request from user: {} for account: {}",
                userDetails.getUsername(), accountNumber);

        // Build request object
        TransactionHistoryRequest request = buildTransactionHistoryRequest(
                accountNumber, startDate, endDate, transactionType, transactionDirection,
                minAmount, maxAmount, sortBy, sortDirection, page, size);

        User currentUser = userService.findUserByEmail(userDetails.getUsername());
        TransactionHistoryResponse response = transactionHistoryService.getTransactionHistory(request, currentUser);

        return ResponseEntity.ok(response);
    }

//

    // getting all transactions by admin
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionHistoryResponse> getAllTransactions(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) String transactionDirection,
            @RequestParam(required = false) String minAmount,
            @RequestParam(required = false) String maxAmount,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @CurrentUser UserDetails userDetails) {

        log.info("Admin requesting all transactions with filters");

        // Build request object (no account number for admin to get all transactions)
        TransactionHistoryRequest request = buildTransactionHistoryRequest(
                null, startDate, endDate, transactionType, transactionDirection,
                minAmount, maxAmount, sortBy, sortDirection, page, size);

        TransactionHistoryResponse response = transactionHistoryService.getAllTransactionsForAdmin(request);

        return ResponseEntity.ok(response);
    }


    // Get specific transaction by reference number
    @GetMapping("/reference/{referenceNumber}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TransactionDetailResponse> getTransactionByReference(
            @PathVariable String referenceNumber,
            @CurrentUser UserDetails userDetails) {

        log.info("Getting transaction by reference: {} for user: {}", referenceNumber, userDetails.getUsername());

        User currentUser = userService.findUserByEmail(userDetails.getUsername());
        TransactionDetailResponse response = transactionHistoryService.getTransactionByReference(referenceNumber, currentUser);

        return ResponseEntity.ok(response);
    }

    // Get specific transaction by ID
    @GetMapping("/id/{transactionId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TransactionDetailResponse> getTransactionById(
            @PathVariable Long transactionId,
            @CurrentUser UserDetails userDetails) {

        log.info("Getting transaction by ID: {} for user: {}", transactionId, userDetails.getUsername());

        User currentUser = userService.findUserByEmail(userDetails.getUsername());
        TransactionDetailResponse response = transactionHistoryService.getTransactionById(transactionId, currentUser);

        return ResponseEntity.ok(response);
    }

    // helper method to build TransactionHistoryRequest with proper validation
    private TransactionHistoryRequest buildTransactionHistoryRequest(
            String accountNumber, String startDate, String endDate, String transactionType,
            String transactionDirection, String minAmount, String maxAmount,
            String sortBy, String sortDirection, int page, int size) {

        TransactionHistoryRequest request = new TransactionHistoryRequest();

        // Set basic parameters
        request.setAccountNumber(accountNumber);
        request.setTransactionType(transactionType);
        request.setTransactionDirection(transactionDirection);
        request.setSortBy(sortBy);
        request.setSortDirection(sortDirection);
        request.setPage(page);
        request.setSize(size);

        // Parse dates with better error handling
        if (startDate != null && !startDate.trim().isEmpty()) {
            try {
                if (startDate.contains("T")) {
                    request.setStartDate(java.time.LocalDateTime.parse(startDate));
                } else {
                    request.setStartDate(java.time.LocalDateTime.parse(startDate + "T00:00:00"));
                }
            } catch (Exception e) {
                log.warn("Invalid start date format: {}, error: {}", startDate, e.getMessage());
            }
        }

        if (endDate != null && !endDate.trim().isEmpty()) {
            try {
                if (endDate.contains("T")) {
                    request.setEndDate(java.time.LocalDateTime.parse(endDate));
                } else {
                    request.setEndDate(java.time.LocalDateTime.parse(endDate + "T23:59:59"));
                }
            } catch (Exception e) {
                log.warn("Invalid end date format: {}, error: {}", endDate, e.getMessage());
            }
        }

        // Parse amounts with better error handling
        if (minAmount != null && !minAmount.trim().isEmpty()) {
            try {
                request.setMinAmount(new java.math.BigDecimal(minAmount.trim()));
            } catch (Exception e) {
                log.warn("Invalid min amount format: {}, error: {}", minAmount, e.getMessage());
            }
        }

        if (maxAmount != null && !maxAmount.trim().isEmpty()) {
            try {
                request.setMaxAmount(new java.math.BigDecimal(maxAmount.trim()));
            } catch (Exception e) {
                log.warn("Invalid max amount format: {}, error: {}", maxAmount, e.getMessage());
            }
        }

        return request;
    }

    @GetMapping("/admin/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionHistoryResponse> getUserTransactions(
            @PathVariable Long userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) String transactionDirection,
            @RequestParam(required = false) String minAmount,
            @RequestParam(required = false) String maxAmount,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @CurrentUser UserDetails userDetails) {

        log.info("Admin {} requesting transactions for user ID: {}", userDetails.getUsername(), userId);

        // Build request object with userId
        TransactionHistoryRequest request = buildTransactionHistoryRequestWithUserId(
                userId, null, startDate, endDate, transactionType, transactionDirection,
                minAmount, maxAmount, sortBy, sortDirection, page, size);

        TransactionHistoryResponse response = transactionHistoryService.getUserTransactionsForAdmin(request);

        return ResponseEntity.ok(response);
    }

    // Updated helper method to include userId
    private TransactionHistoryRequest buildTransactionHistoryRequestWithUserId(
            Long userId, String accountNumber, String startDate, String endDate, String transactionType,
            String transactionDirection, String minAmount, String maxAmount,
            String sortBy, String sortDirection, int page, int size) {

        TransactionHistoryRequest request = buildTransactionHistoryRequest(
                accountNumber, startDate, endDate, transactionType, transactionDirection,
                minAmount, maxAmount, sortBy, sortDirection, page, size);

        // Set the userId
        request.setUserId(userId);

        return request;
    }

}