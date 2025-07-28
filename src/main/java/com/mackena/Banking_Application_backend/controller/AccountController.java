package com.mackena.Banking_Application_backend.controller;

import com.mackena.Banking_Application_backend.dtos.response.AccountListResponse;
import com.mackena.Banking_Application_backend.dtos.response.AccountResponse;
import com.mackena.Banking_Application_backend.dtos.response.ApiResponse;
import com.mackena.Banking_Application_backend.dtos.response.TotalBalanceResponse;
import com.mackena.Banking_Application_backend.models.entity.Account;
import com.mackena.Banking_Application_backend.models.entity.User;
import com.mackena.Banking_Application_backend.repository.AccountRepository;
import com.mackena.Banking_Application_backend.security.CurrentUser;
import com.mackena.Banking_Application_backend.service.AccountService;
import com.mackena.Banking_Application_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;
    private final AccountRepository accountRepository;

    //get all accounts for the user
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<AccountListResponse> getUserAccounts(@CurrentUser UserDetails userDetails) {
        log.info("Getting accounts for user: {}", userDetails.getUsername());

        User user = userService.findUserByEmail(userDetails.getUsername());
        AccountListResponse response = accountService.getUserAccounts(user.getId());

        return ResponseEntity.ok(response);
    }

    //Get a specific account for user
    @GetMapping("/{accountNumber}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<AccountResponse> getAccountByNumber(
            @PathVariable String accountNumber,
            @CurrentUser UserDetails userDetails) {

        log.info("Getting account details for: {} by user: {}", accountNumber, userDetails.getUsername());

        User user = userService.findUserByEmail(userDetails.getUsername());
        boolean isAdmin = user.getRole().isAdmin();

        AccountResponse response = accountService.getAccountByNumber(accountNumber, user.getId(), isAdmin);

        return ResponseEntity.ok(response);
    }

    // FIXED: Combined both low balance methods into one comprehensive method
    @GetMapping("/low-balance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getLowBalanceAccounts(
            @RequestParam(defaultValue = "100") BigDecimal threshold,
            @CurrentUser UserDetails userDetails) {

        log.info("Admin {} requesting low balance accounts with threshold: {}",
                userDetails.getUsername(), threshold);

        try {
            List<Account> lowBalanceAccounts = accountService.findAccountsWithBalanceBelow(threshold);

            Map<String, Object> response = Map.of(
                    "accounts", lowBalanceAccounts,
                    "count", lowBalanceAccounts.size(),
                    "threshold", threshold
            );

            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Low balance accounts retrieved successfully")
                    .data(response)
                    .build());

        } catch (Exception e) {
            log.error("Error retrieving low balance accounts", e);
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .success(false)
                    .message("Failed to retrieve low balance accounts")
                    .build());
        }
    }

    // Get total system balance (admin only)
    @GetMapping("/total-balance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getTotalSystemBalance(@CurrentUser UserDetails userDetails) {
        log.info("Admin {} requesting total system balance", userDetails.getUsername());

        try {
            TotalBalanceResponse totalBalance = accountService.getTotalSystemBalance();
            long totalAccounts = accountService.getTotalAccountCount();
            long activeAccounts = accountService.getActiveAccountCount();

            Map<String, Object> response = Map.of(
                    "totalBalance", totalBalance,
                    "totalAccounts", totalAccounts,
                    "activeAccounts", activeAccounts
            );

            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Total system balance retrieved successfully")
                    .data(response)
                    .build());

        } catch (Exception e) {
            log.error("Error retrieving total system balance", e);
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .success(false)
                    .message("Failed to retrieve total system balance")
                    .build());
        }
    }

    // Validate account for transfer - specific endpoint for validation
    @GetMapping("/validate-for-transfer/{accountNumber}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> validateAccountForTransfer(
            @PathVariable String accountNumber) {

        log.info("Validating account for transfer: {}", accountNumber);

        try {
            // Simple existence check - no ownership required
            Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber.trim().toUpperCase());

            if (accountOpt.isPresent() && accountOpt.get().isActive()) {
                Map<String, Object> validationData = Map.of(
                        "valid", true,
                        "accountExists", true,
                        "isActive", true,
                        "canReceiveTransfers", true
                );

                return ResponseEntity.ok(ApiResponse.builder()
                        .success(true)
                        .message("Account is valid for transfers")
                        .data(validationData)
                        .build());
            } else {
                Map<String, Object> validationData = Map.of(
                        "valid", false,
                        "accountExists", accountOpt.isPresent(),
                        "isActive", accountOpt.isPresent() ? accountOpt.get().isActive() : false,
                        "canReceiveTransfers", false
                );

                return ResponseEntity.ok(ApiResponse.builder()
                        .success(true)
                        .message("Account not found or inactive")
                        .data(validationData)
                        .build());
            }

        } catch (Exception e) {
            log.error("Error validating account for transfer: {}", accountNumber, e);

            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Account validation failed")
                    .data(Map.of(
                            "valid", false,
                            "accountExists", false,
                            "isActive", false,
                            "canReceiveTransfers", false
                    ))
                    .build());
        }
    }
    // Get account balance only
    @GetMapping("/{accountNumber}/balance")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getAccountBalance(
            @PathVariable String accountNumber,
            @CurrentUser UserDetails userDetails) {

        log.info("Getting balance for account: {} by user: {}", accountNumber, userDetails.getUsername());

        try {
            User currentUser = userService.findUserByEmail(userDetails.getUsername());
            Account account = accountService.findByAccountNumber(accountNumber);

            // Check if user owns the account or is admin
            if (!account.getUser().getId().equals(currentUser.getId()) &&
                    !currentUser.getRole().name().equals("ADMIN")) {
                return ResponseEntity.status(403).body(ApiResponse.builder()
                        .success(false)
                        .message("Access denied: You can only view your own account balance")
                        .build());
            }

            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Balance retrieved successfully")
                    .data(Map.of("balance", account.getBalance()))
                    .build());

        } catch (Exception e) {
            log.error("Error retrieving balance for account: {}", accountNumber, e);
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .success(false)
                    .message("Failed to retrieve balance")
                    .build());
        }
    }

    // Search accounts (admin only)
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> searchAccounts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @CurrentUser UserDetails userDetails) {

        log.info("Admin {} searching accounts with query: {}", userDetails.getUsername(), query);

        try {
            List<Account> accounts = accountService.searchAccounts(query, page, size);

            Map<String, Object> response = Map.of(
                    "accounts", accounts,
                    "totalResults", accounts.size(),
                    "page", page,
                    "size", size
            );

            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Account search completed successfully")
                    .data(response)
                    .build());

        } catch (Exception e) {
            log.error("Error searching accounts with query: {}", query, e);
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .success(false)
                    .message("Account search failed")
                    .build());
        }
    }
}