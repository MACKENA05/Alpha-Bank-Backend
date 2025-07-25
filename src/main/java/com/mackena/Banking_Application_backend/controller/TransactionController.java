package com.mackena.Banking_Application_backend.controller;

import com.mackena.Banking_Application_backend.dtos.request.*;
import com.mackena.Banking_Application_backend.dtos.response.*;
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

    // Withdrawal endpoint - Only users can withdraw from their own accounts
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

    // Deposit endpoint - Only admins can deposit
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

    // Transfer endpoint - Users can transfer from their own accounts
    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TransferResponse> transferMoney(
            @Valid @RequestBody TransferRequest request,
            @CurrentUser UserDetails userDetails) {

        log.info("Transfer request from user: {} for amount: {}",
                userDetails.getUsername(), request.getAmount());

        User currentUser = userService.findUserByEmail(userDetails.getUsername());
        TransferResponse response = transferService.transferMoney(request, currentUser);

        return ResponseEntity.ok(response);
    }

    // Transaction history endpoint
    @GetMapping("/history")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TransactionHistoryResponse> getTransactionHistory(
            @ModelAttribute TransactionHistoryRequest request,
            @CurrentUser UserDetails userDetails) {

        User currentUser = userService.findUserByEmail(userDetails.getUsername());
        TransactionHistoryResponse response = transactionHistoryService.getTransactionHistory(request, currentUser);

        return ResponseEntity.ok(response);
    }
}