package com.mackena.Banking_Application_backend.controller;

import com.mackena.Banking_Application_backend.dtos.response.AccountListResponse;
import com.mackena.Banking_Application_backend.dtos.response.AccountResponse;
import com.mackena.Banking_Application_backend.dtos.response.TotalBalanceResponse;
import com.mackena.Banking_Application_backend.models.entity.Account;
import com.mackena.Banking_Application_backend.models.entity.User;
import com.mackena.Banking_Application_backend.security.CurrentUser;
import com.mackena.Banking_Application_backend.service.AccountService;
import com.mackena.Banking_Application_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.mackena.Banking_Application_backend.dto.response.LowBalanceAccountsResponse;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;

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

    //get low balance accounts
    @GetMapping("/low-balance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LowBalanceAccountsResponse> getLowBalanceAccounts(
            @RequestParam(defaultValue = "100") BigDecimal threshold) {

        LowBalanceAccountsResponse response = accountService.getLowBalanceAccounts(threshold);

        return ResponseEntity.ok(response);
    }

    //get total system balance
    @GetMapping("/total-balance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TotalBalanceResponse> getTotalSystemBalance(){
        TotalBalanceResponse response = accountService.getTotalSystemBalance();
        return ResponseEntity.ok(response);
    }
}
