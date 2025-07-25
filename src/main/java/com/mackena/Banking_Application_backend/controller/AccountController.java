package com.mackena.Banking_Application_backend.controller;

import com.mackena.Banking_Application_backend.dtos.response.AccountListResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<AccountListResponse> getUserAccounts(@CurrentUser UserDetails userDetails) {
        log.info("Getting accounts for user: {}", userDetails.getUsername());

        User user = userService.findUserByEmail(userDetails.getUsername());
        AccountListResponse response = accountService.getUserAccounts(user.getId());

        return ResponseEntity.ok(response);
    }
}
