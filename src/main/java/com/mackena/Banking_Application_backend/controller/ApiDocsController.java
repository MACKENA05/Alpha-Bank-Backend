package com.mackena.Banking_Application_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/docs")
public class ApiDocsController {

    @GetMapping("/endpoints")
    public ResponseEntity<Map<String, Object>> getApiEndpoints() {
        Map<String, Object> endpoints = new HashMap<>();

        // Auth endpoints
        endpoints.put("auth", Map.of(
                "register", "POST /api/auth/register",
                "login", "POST /api/auth/login",
                "logout", "POST /api/auth/logout"
        ));

        // Account endpoints
        endpoints.put("accounts", Map.of(
                "getUserAccounts", "GET /api/accounts",
                "getAccountByNumber", "GET /api/accounts/{accountNumber}",
                "getLowBalanceAccounts", "GET /api/accounts/low-balance",
                "getTotalSystemBalance", "GET /api/accounts/total-balance"
        ));

        // Transaction endpoints
        endpoints.put("transactions", Map.of(
                "withdraw", "POST /api/transactions/withdraw",
                "deposit", "POST /api/transactions/deposit",
                "transfer", "POST /api/transactions/transfer",
                "getHistory", "GET /api/transactions/history",
                "getAllTransactions", "GET /api/transactions/admin/all"
        ));

        return ResponseEntity.ok(endpoints);
    }
}
